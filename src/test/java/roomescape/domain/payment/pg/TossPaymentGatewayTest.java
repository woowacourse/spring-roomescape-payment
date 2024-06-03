package roomescape.domain.payment.pg;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.domain.payment.exception.PaymentConfirmClientFailException;
import roomescape.domain.payment.exception.PaymentConfirmServerFailException;

import static roomescape.domain.payment.config.PaymentApiUrl.PG_API_BASE_URL;

class TossPaymentGatewayTest {

    @DisplayName("토스 페이먼츠 API 응답으로 4xx가 응답되면 예외를 발생시킨다.")
    @Test
    void throwExceptionWhenResponse4xx() {
        // Given
        final RestClient restClient = RestClient.builder()
                .baseUrl(PG_API_BASE_URL)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set(HttpHeaders.AUTHORIZATION, "Basic dGVzdF9za196WExrS0V5cE5BcldtbzUwblgzbG1lYXhZRzVSOg==");
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    httpHeaders.set("TossPayments-Test-Code", "PAY_PROCESS_ABORTED");
                })
                .build();
        final TossPaymentGateway tossPaymentGateway = new TossPaymentGateway(restClient);

        // When & Then
        Assertions.assertThatThrownBy(() -> tossPaymentGateway.confirm(
                        "test-order-id",
                        10000L,
                        "test-payment-key"))
                .isInstanceOf(PaymentConfirmClientFailException.class);
    }

    @DisplayName("토스 페이먼츠 API 응답으로 5xx가 응답되면 예외를 발생시킨다.")
    @Test
    void throwExceptionWhenResponse5xx() {
        // Given
        final RestClient restClient = RestClient.builder()
                .baseUrl(PG_API_BASE_URL)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set(HttpHeaders.AUTHORIZATION, "Basic dGVzdF9za196WExrS0V5cE5BcldtbzUwblgzbG1lYXhZRzVSOg==");
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    httpHeaders.set("TossPayments-Test-Code", "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING");
                })
                .build();
        final TossPaymentGateway tossPaymentGateway = new TossPaymentGateway(restClient);

        // When & Then
        Assertions.assertThatThrownBy(() -> tossPaymentGateway.confirm(
                        "test-order-id",
                        10000L,
                        "test-payment-key"))
                .isInstanceOf(PaymentConfirmServerFailException.class);
    }
}
