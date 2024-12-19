package com.playdata.orderingservice.ordering.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderingSaveReqDto {

    // 토큰에서 사용자를 식별할 수 있기 때문에 userId 같은 것은 받을 필요가 없다.

    private Long productId;
    private int productCount;

}
