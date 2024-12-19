package com.playdata.orderingservice.common.configs;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreaker;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/*
    서킷 브레이커 (Circuit Breaker)
    - 서비스 호출 실패율이 일정 기준을 넘을 때, 호출을 차단(Open)하여 추가적인 실패를 방지하는 패턴.

    Open: 실패율이 기준을 초과해서 열림 상태로 전환. 요청을 차단하고 즉시 예외를 반환
    Closed: 정상 상태. 요청이 성공적으로 처리되면 원래의 상태 유지.
    Half-Open: 일정 시간이 지나 테스트 요청을 보낸 후 성공 여부에 따라 상태를 결정
*/

@Configuration
@Slf4j
public class Resilience4JConfig {

    @Bean
    // Resilience4j의 설정을 커스텀 할 때는 Customizer 인터페이스의 구현체로 리턴해야 합니다.
    public Customizer<Resilience4JCircuitBreakerFactory> circuitBreakerFactory() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(4) // 요청 실패 비율이 설정값을 넘게 되면 서킷브레이커가 open됨.
                .waitDurationInOpenState(Duration.ofMillis(1000)) // open 상태에서 유지 시간: 1초
                // 슬라이딩 윈도우(일정한 크기의 요청 집합) 기법을 사용하여 최신 요청 데이터를 지속적으로 갱신
                // COUNT_BASED: 고정된 요청 수를 기반으로 실패율 계산
                // TIME_BASED: 고정된 시간 간격동안 발생한 요청 기준으로 실패율 계산
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(5)
                .build();

        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                // 요청 실행 시간이 4초를 초과하면 타임아웃 -> 실패로 간주
                .timeoutDuration(Duration.ofSeconds(4))
                .build();

        // 위에 미리 선언한 config 클래스를 Builder로 하나로 합치고, 합친 설정 내용을
        // 최종적으로 factory 메서드로 전달해서 빈으로 등록.
        return factory -> factory.configureDefault(
                id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(timeLimiterConfig)
                .circuitBreakerConfig(circuitBreakerConfig)
                .build());
    }

}
















