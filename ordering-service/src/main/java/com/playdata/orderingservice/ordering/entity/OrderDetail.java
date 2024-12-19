package com.playdata.orderingservice.ordering.entity;

import com.playdata.orderingservice.ordering.dto.OrderingListResDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    @JoinColumn
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordering_id")
    private Ordering ordering;


    // 엔터티를 dto로 변환하는 메서드
    // 내부 클래스이기 때문에 OrderingListResDto 이름으로 참조하는 모습.
    public OrderingListResDto.OrderDetailDto fromEntity(Map<Long, String> productIdToNameMap) {
        String productName = productIdToNameMap.get(productId);
        return OrderingListResDto.OrderDetailDto.builder()
                .id(id)
                .productName(productName)
                .count(quantity)
                .build();
    }


}

















