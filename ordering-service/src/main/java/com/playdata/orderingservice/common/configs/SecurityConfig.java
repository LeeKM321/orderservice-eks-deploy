package com.playdata.orderingservice.common.configs;

import com.playdata.orderingservice.common.auth.JwtAuthFilter;
import com.playdata.orderingservice.common.dto.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 권한 검사를 컨트롤러의 메서드에서 전역적으로 수행하기 위한 설정.
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    // 시큐리티 기본 설정 (권한 처리, 초기 로그인 화면 없애기 등등...)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrfConfig -> csrfConfig.disable());
//        http.cors(Customizer.withDefaults()); // 직접 커스텀한 CORS 설정을 적용하겠다.
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> {
            auth
//                    .requestMatchers("/user/list").hasAnyRole("ADMIN")
                    .requestMatchers("/create", "/doLogin",
                            "/refresh", "/").permitAll()
                    .anyRequest().authenticated();
        })
                // 커스텀 필터를 등록.
                // 시큐리티에서 기본으로 인증, 인가 처리를 해 주는 UsernamePasswordAuthenticationFilter 전에 내 필터 add
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }



}
