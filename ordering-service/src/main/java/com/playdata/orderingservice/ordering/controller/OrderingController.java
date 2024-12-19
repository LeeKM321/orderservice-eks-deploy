package com.playdata.orderingservice.ordering.controller;

import com.playdata.orderingservice.common.auth.TokenUserInfo;
import com.playdata.orderingservice.common.dto.CommonResDto;
import com.playdata.orderingservice.ordering.dto.OrderingListResDto;
import com.playdata.orderingservice.ordering.dto.OrderingSaveReqDto;
import com.playdata.orderingservice.ordering.entity.Ordering;
import com.playdata.orderingservice.ordering.service.OrderingService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Slf4j
public class OrderingController {

    private final OrderingService orderingService;

    @PostMapping("/create")
    public ResponseEntity<?> orderingCreate(
            // 전역 인증 정보를 담아놓는 ContextHolder에서 메서드 호출시에
            // 사용자 인증 정보를 전달해 달라는 아노테이션
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestBody List<OrderingSaveReqDto> dtoList) {
        Ordering ordering = orderingService.createOrdering(dtoList, userInfo);

        CommonResDto resDto
                = new CommonResDto(HttpStatus.CREATED, "정상 주문 완료", ordering.getId());

        return new ResponseEntity<>(resDto, HttpStatus.CREATED);
    }

//     내 주문만 볼 수 있는 myOrders
    @GetMapping("/my-order")
    public ResponseEntity<?> myOrder(@AuthenticationPrincipal TokenUserInfo userInfo) {
        List<OrderingListResDto> dtos = orderingService.myOrders(userInfo);

        CommonResDto resDto
                = new CommonResDto(HttpStatus.OK, "정상 조회 완료", dtos);

        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 전체 주문 조회 (ADMIN만 가능한 요청)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<?> list() {
        List<OrderingListResDto> dtos = orderingService.orderList();
        return new ResponseEntity<>(
                new CommonResDto(HttpStatus.OK, "전체 주문 조회 완료", dtos),
                HttpStatus.OK
        );
    }

    // 주문 상태를 취소로 변경하는 요청 (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> orderCancel(@PathVariable long id) {
        Ordering ordering = orderingService.orderCancel(id);
        CommonResDto resDto
                = new CommonResDto(HttpStatus.OK, "주문 취소 완료", ordering.getId());
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }


}

















