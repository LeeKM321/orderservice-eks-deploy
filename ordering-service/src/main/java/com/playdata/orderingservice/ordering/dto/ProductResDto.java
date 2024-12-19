package com.playdata.orderingservice.ordering.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResDto {

    private Long id;
    private String name;
    private String category;
    private Integer price;
    private Integer stockQuantity;

}