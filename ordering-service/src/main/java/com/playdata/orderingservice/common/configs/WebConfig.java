package com.playdata.orderingservice.common.configs;

import com.playdata.orderingservice.client.FeignErrorDecorder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @LoadBalanced // 유레카에 등록된 서비스명을 사용해서, 내부 서비스를 호출할 수 있게 해 주는 어노테이션.
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public FeignErrorDecorder getFeignErrorDecorder() {
        return new FeignErrorDecorder();
    }

}
