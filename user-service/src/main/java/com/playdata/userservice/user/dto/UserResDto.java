package com.playdata.userservice.user.dto;

import com.playdata.userservice.common.entity.Address;
import com.playdata.userservice.user.entity.Role;
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














