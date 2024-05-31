package roomescape.learningtest.api;

import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import roomescape.dto.PaymentErrorResponse;
import roomescape.exception.PaymentException;
import roomescape.exception.PaymentExceptionType;

public class TossExceptionApi {
    private final RestClient restClient;

    public TossExceptionApi() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .build();
    }

    public void raiseExceptionOf(String exceptionCode) {
        try {
            restClient.post()
                    .uri("v1/payments/key-in")
                    .header("Authorization", "Basic dGVzdF9za196WExrS0V5cE5BcldtbzUwblgzbG1lYXhZRzVSOg==")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("TossPayments-Test-Code", exceptionCode)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            PaymentErrorResponse errorResponse = e.getResponseBodyAs(PaymentErrorResponse.class);
            throw new PaymentException(PaymentExceptionType.findBy(errorResponse.code() ), e);
        }
    }
}
