package com.playdata.orderingservice.ordering.service;

import com.playdata.orderingservice.client.ProductServiceClient;
import com.playdata.orderingservice.client.UserServiceClient;
import com.playdata.orderingservice.common.auth.TokenUserInfo;
import com.playdata.orderingservice.common.dto.CommonResDto;
import com.playdata.orderingservice.ordering.controller.SseController;
import com.playdata.orderingservice.ordering.dto.OrderingListResDto;
import com.playdata.orderingservice.ordering.dto.OrderingSaveReqDto;
import com.playdata.orderingservice.ordering.dto.ProductResDto;
import com.playdata.orderingservice.ordering.dto.UserResDto;
import com.playdata.orderingservice.ordering.entity.OrderDetail;
import com.playdata.orderingservice.ordering.entity.OrderStatus;
import com.playdata.orderingservice.ordering.entity.Ordering;
import com.playdata.orderingservice.ordering.repository.OrderingRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.internal.DDLFormatterImpl;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderingService {

    private final OrderingRepository orderingRepository;
    private final SseController sseController;
    private final RestTemplate template;

    // feign client 구현체 주입 받기
    private final UserServiceClient userServiceClient;
    private final ProductServiceClient productServiceClient;

    private final CircuitBreakerFactory circuitBreakerFactory;


    public Ordering createOrdering(List<OrderingSaveReqDto> dtoList,
                                   TokenUserInfo userInfo) {
        // Ordering 객체를 생성하기 위해 회원 정보를 얻어오기.
        // 우리가 가진 유일한 정보는 토큰 안에 들어있던 이메일 뿐입니다.
        // 이메일을 가지고 요청을 보내자 -> user-service
        CommonResDto<UserResDto> byEmail
                = userServiceClient.findByEmail(userInfo.getEmail());
        UserResDto userDto = byEmail.getResult();
        log.info("userDto: {}", userDto);

        // Ordering(주문) 객체 생성
        Ordering ordering = Ordering.builder()
                .userId(userDto.getId())
                .orderDetails(new ArrayList<>()) // 아직 주문 상세 들어가기 전.
                .build();

        // 주문 상세 내역에 대한 처리를 반복문으로 지정.
        for (OrderingSaveReqDto dto : dtoList) {

            // dto에는 상품 고유 id가 있으니까 그걸 활용해서
            // product 객체를 조회하자. -> product-service에게 요청해야 함!

            CommonResDto<ProductResDto> commonResDto
                    = productServiceClient.findById(dto.getProductId());
            ProductResDto prodResDto = commonResDto.getResult();

            // 재고 넉넉하게 있는지 확인.
            int quantity = dto.getProductCount();
            if (prodResDto.getStockQuantity() < quantity) {
                throw new IllegalArgumentException("재고 부족!");
            }

            // 재고가 부족하지 않다면 재고 수량을 주문 수량만큼 빼 주자.
            // product-service에게 재고 수량이 변경되었다고 알려주자.
            // 상품 id와 변경되어야 할 재고 수량을 함께 보내주자.
            prodResDto.setStockQuantity(prodResDto.getStockQuantity() - quantity);
            productServiceClient.updateQuantity(prodResDto);

            // 주문 상세 내역 엔터티를 생성
            OrderDetail orderDetail = OrderDetail.builder()
                    .productId(dto.getProductId())
                    .ordering(ordering)
                    .quantity(quantity)
                    .build();

            // 주문 내역 리스트에 상세 내역을 add 하기.
            // (cascadeType.PERSIST로 세팅했기 때문에 함께 add가 진행될 것.)
            ordering.getOrderDetails().add(orderDetail);
        } // end forEach

        // Ordering 객체를 save하면 내부에 있는 detail 리스트도 함께 INSERT가 진행이 된다.
        Ordering save = orderingRepository.save(ordering);

        // 관리자에게 주문이 생성되었다는 알림을 전송
        sseController.sendOrderMessage(userDto);

        return save;

    }


    public List<OrderingListResDto> myOrders(TokenUserInfo userInfo) {

        String userEmail = userInfo.getEmail();
        // feign client 이용해서 user 정보 얻어오기
//        CommonResDto<UserResDto> byEmail = userServiceClient.findByEmail(userEmail);

        CircuitBreaker circuitBreaker
                = circuitBreakerFactory.create("circuitBreaker1");

        CommonResDto<UserResDto> byEmail
                = circuitBreaker.run(
                () -> userServiceClient.findByEmail(userEmail),
                throwable -> null);

        if (byEmail == null) {
            log.error("byEmail is null!");
        }

        UserResDto userResDto = byEmail.getResult();

        // 해당 사용자의 주문 내역 전부 가져오기.
        List<Ordering> orderingList
                = orderingRepository.findByUserId(userResDto.getId());

        // 주문 내역에서 모든 상품 ID 추출해야 함.
        List<Long> productIds = getProductIds(orderingList);

        // Product-service에게 상품 정보를 달라고 요청해야 함.
        CommonResDto<List<ProductResDto>> products =
                circuitBreaker.run(
                        () -> productServiceClient.getProducts(productIds),
                        throwable -> new CommonResDto<>(HttpStatus.OK, "server error", new ArrayList<>())
                );


        List<ProductResDto> dtoList = products.getResult();

        // product-service에게 받아온 리스트를 필요로 하는 정보로만 맵으로 매핑.
        Map<Long, String> productIdToNameMap = getProductIdToNameMap(dtoList);

        // Ordering 엔터티를 DTO로 변환하자. 주문 상세에 대한 변환도 필요하다!
        List<OrderingListResDto> dtos = orderingList.stream()
                .map(order -> order.fromEntity(userInfo.getEmail(), productIdToNameMap))
                .collect(Collectors.toList());

        return dtos;
    }


    public List<OrderingListResDto> orderList() {
        // 1. 회원 구분 없이 모든 주문 내역을 전체 조회하기
        List<Ordering> orderList = orderingRepository.findAll();

        // 2. 모든 주문에서 필요한 user Id와 Product Id를 추출하자.
        List<Long> userIds = orderList.stream()
                .map(order -> order.getUserId())
                .distinct()
                .collect(Collectors.toList());

        List<Long> productIds = getProductIds(orderList);

        // 3. Feign Client로 사용자 정보와 상품 정보 조회
        CommonResDto<List<UserResDto>> usersByIds
                = userServiceClient.getUsersByIds(userIds);
        List<UserResDto> userResDtoList = usersByIds.getResult();

        CommonResDto<List<ProductResDto>> products
                = productServiceClient.getProducts(productIds);
        List<ProductResDto> productDtoList = products.getResult();

        // 4. fromEntity 메서드의 매개값으로 적당한 데이터를 전달해야 한다.
        // 기존의 fromEntity는 이메일은 단일값, 상품명은 id와 상품명을 하나로 맵핑한 Map

        // 회원 번호와 회원의 이메일을 Map으로 맵핑했습니다.
        // id를 통해 email을 쉽게 얻어내기 위해서.
        Map<Long, String> userIdToEmailMap = userResDtoList.stream()
                .collect(Collectors.toMap(
                        dto -> dto.getId(),
                        dto -> dto.getEmail()
                ));

        // 밑에 있는 중복 로직 추출 메서드 호출.
        Map<Long, String> productIdToNameMap = getProductIdToNameMap(productDtoList);

        /*
        List<OrderingListResDto> dtos = new ArrayList<>();
        for (Ordering ordering : orderList) {
            Long userId = ordering.getUserId();
            String email = userIdToEmailMap.get(userId);
            OrderingListResDto orderingListResDto
                    = ordering.fromEntity(email, productIdToNameMap);
            dtos.add(orderingListResDto);
        }
        */

        List<OrderingListResDto> dtos = orderList.stream()
                .map(order -> {
                    Long userId = order.getUserId(); // 주문한 회원의 번호
                    return order.fromEntity(userIdToEmailMap.get(userId), productIdToNameMap);
                })
                .collect(Collectors.toList());

        return dtos;
    }


    public Ordering orderCancel(long id) {
        // 상태를 CANCEL로 변경해 주세요.
        // 클라이언트에게는 변경 상태와 주문 id만 넘겨 주세요.
        Ordering ordering = orderingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("주문 없는데요!")
        );

        ordering.updateStatus(OrderStatus.CANCELED); // 더티 체킹 (save를 하지 않아도 변경을 감지한다.)
        return ordering;
    }


    // 중복 로직 메서드 추출 (상품 아이디와 상품명 맵핑, 주문내역에서 상품번호만 뽑기)
    private Map<Long, String> getProductIdToNameMap(List<ProductResDto> dtoList) {
        Map<Long, String> productIdToNameMap = dtoList.stream()
                .collect(Collectors.toMap(
                        dto -> dto.getId(), // -> key
                        dto -> dto.getName()));// -> value로 맵핑
        return productIdToNameMap;
    }

    private List<Long> getProductIds(List<Ordering> orderingList) {
        List<Long> productIds = orderingList.stream() // 스트림 준비
                // flatMap: 하나의 주문 내역에서 상세 주문 내역 리스트를 꺼낸 후 하나의 스트림으로 평탄화
                /* flatMap의 동작 원리
                [
                    Ordering 1 -> [OrderDetail1, OrderDetail2]
                    Ordering 2 -> [OrderDetail3]
                    Ordering 3 -> [OrderDetai4, OrderDetail5, OrderDetail6]
                ]

                [OrderDetail1, OrderDetail2, OrderDetail3, OrderDetail4, OrderDetail5, OrderDetail6]
                 */
                .flatMap(order -> order.getOrderDetails().stream())
                .map(orderDetail -> orderDetail.getProductId())
                .distinct()
                .collect(Collectors.toList());
        return productIds;
    }


}




















