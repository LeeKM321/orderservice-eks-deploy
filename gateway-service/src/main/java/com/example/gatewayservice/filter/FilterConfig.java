//package com.example.gatewayservice.filter;
//
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.cors.reactive.CorsUtils;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import reactor.core.publisher.Mono;
//
//// 게이트웨이 라우팅 및 필터 등록 방식은 크게 2가지
//// 클래스 빈 등록 방식, yml에 등록하는 방식.
//@Configuration
//public class FilterConfig {
//
//    @Bean
//    public WebFilter corsFilter() {
//        return (exchange, chain) -> {
//            ServerWebExchange request = exchange;
//
//            if (CorsUtils.isCorsRequest(request.getRequest())) {
//                HttpHeaders headers = exchange.getResponse().getHeaders();
//
//                headers.add("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS");
//                headers.add("Access-Control-Max-Age", "3600");
//
//                if (request.getRequest().getMethod() == HttpMethod.OPTIONS) {
//                    headers.add("Access-Control-Allow-Credentials", "true");
//                    headers.add("Access-Control-Allow-Origin", "http://localhost:3000");
//                    headers.add("Access-Control-Allow-Headers",
//                            "x-requested-with, authorization, Content-Type, Authorization, credential, X-XSRF-TOKEN");
//
//                    exchange.getResponse().setStatusCode(HttpStatus.OK);
//                    return Mono.empty();
//                }
//            }
//
//            return chain.filter(exchange);
//        };
//    }
//
//
//    /*
////    @Bean
//    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route(r -> r.path("/first-service/**")
//                        .filters(f -> f.addRequestHeader("first-request", "first-request-header"))
//                        .uri("http://localhost:8181"))
//
//                .route(r -> r.path("/second-service/**")
//                        .filters(f -> f.addRequestHeader("second-request", "second-request-header"))
//                        .uri("http://localhost:8282"))
//
//                .build();
//    }
//
//     */
//
//}
