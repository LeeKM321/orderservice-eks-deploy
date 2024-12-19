package com.playdata.userservice.user.entity;

import com.playdata.userservice.common.entity.Address;
import com.playdata.userservice.user.dto.UserResDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter @NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Embedded // @Embeddable로 선언된 값 대입 (기본 생성자 필수)
    private Address address;

    @Enumerated(EnumType.STRING)
    @Builder.Default // build시 초기화된 값으로 세팅하기 위한 아노테이션
    private Role role = Role.USER;

    private String refreshToken;


    public UserResDto fromEntity() {
        return UserResDto.builder()
                .id(id)
                .name(name)
                .email(email)
                .role(role)
                .address(address)
                .build();
    }


}















