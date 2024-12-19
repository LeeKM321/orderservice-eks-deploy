package com.playdata.orderingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients // Feign Client를 사용하는 서비스에 추가.
public class OrderingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderingServiceApplication.class, args);
    }

}
