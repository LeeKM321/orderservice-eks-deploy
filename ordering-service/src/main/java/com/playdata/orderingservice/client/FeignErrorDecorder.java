package com.playdata.orderingservice.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

// Feign Client로부터 받은 응답 결과 코드를 각각의 상황에 맞는 예외로 변환할 수 있는 클래스.
// 우리가 각 상황에 맞는 예외 타입들을 지정을 해놨는데, 그게 Feign으로 요청되는 거라면
// 예외 타입에 상관없이 Feign Exception으로 통합됨. -> 500 에러로 취급됨.
// FeignErrorDecorder를 통해 특정 메서드를 호출했을 때 응답되는 결과에 따라
// 원하는 타입의 예외 및 메세지를 커스텀해서 응답하는 것이 가능.
public class FeignErrorDecorder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        // methodKey: feign client에서 호출한 메서드명.
        switch (response.status()) {
            case 400:
                break;
            case 403:
                if (methodKey.contains("getUsersByIds")) {
                    return new ResponseStatusException(
                            HttpStatus.FORBIDDEN, "security permitAll error");
                }
                break;
            case 404:
                break;
        }


        return null;
    }
}
