package roomescape.learningtest.api;

import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import roomescape.dto.PaymentErrorResponse;
import roomescape.exception.PaymentException;
import roomescape.api.TossPaymentExceptionType;

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
            TossPaymentExceptionType tossPaymentExceptionType = TossPaymentExceptionType.findBy(errorResponse.code());
            throw new PaymentException(tossPaymentExceptionType.getHttpStatus(), tossPaymentExceptionType.getMessage(), e);
        }
    }
}
