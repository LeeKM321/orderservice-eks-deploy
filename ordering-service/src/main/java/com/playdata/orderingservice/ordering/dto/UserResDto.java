package com.playdata.orderingservice.ordering.dto;

import com.playdata.orderingservice.common.entity.Address;
import com.playdata.orderingservice.common.entity.Role;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResDto {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private Address address;



}

