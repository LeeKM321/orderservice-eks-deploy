package com.playdata.orderingservice.ordering.entity;

import com.playdata.orderingservice.ordering.dto.OrderingListResDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
public class Ordering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    */

    // 프로젝트가 나눠지면서 Order 쪽에서는 User 엔터티에 대한 정보를 확인할 수 없다.
    // 클라이언트 단에서 넘어오는 정보만 저장할 수 있다.
    @JoinColumn
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.ORDERED;

    @OneToMany(mappedBy = "ordering", cascade = CascadeType.PERSIST)
    private List<OrderDetail> orderDetails;


    public OrderingListResDto fromEntity(String email, Map<Long, String> productIdToNameMap) {

        // DB에서 조회해 온 Ordering에서 상세 내역을 확인합니다.
        List<OrderDetail> orderDetailList = this.getOrderDetails();
        List<OrderingListResDto.OrderDetailDto> orderDetailDtos = new ArrayList<>();

        // OrderDetail 엔터티를 OrderDetailDto로 변환합시다.
        // 변환한 후에는 리스트에 추가합니다.
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetailDtos.add(orderDetail.fromEntity(productIdToNameMap));
        }

        // 주문 상세 내역 dto 포장이 완료되면 하나의 주문 내역 자체를 dto로 변환해서 리턴.
        return OrderingListResDto.builder()
                .id(this.id)
                .userEmail(email)
                .orderStatus(this.orderStatus)
                .orderDetails(orderDetailDtos)
                .build();
    }

    public void updateStatus(OrderStatus status) {
        this.orderStatus = status;
    }

}













