package roomescape.reservation.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.exception.PaymentException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class PaymentApiResponseTest {

    @DisplayName("PaymentRequest 데이터 검증 후 Payment 객체로 변환한다.")
    @Test
    void convertDomainTest() {
        // Given
        PaymentRequest request = new PaymentRequest("paymentKey", "orderId", 1_000L);
        PaymentApiResponse response = new PaymentApiResponse("paymentKey", "orderId", 1_000L, "status");

        // When & Then
        assertThatCode(() -> response.toEntity(request)).doesNotThrowAnyException();
    }

    @DisplayName("검증 오류 시 PaymentException을 던진다.")
    @Test
    void convertDomainValidationTest() {
        // Given
        PaymentRequest request1 = new PaymentRequest("wrong paymentKey", "orderId", 1_000L);
        PaymentRequest request2 = new PaymentRequest("paymentKey", "wrong orderId", 1_000L);
        PaymentRequest request3 = new PaymentRequest("paymentKey", "orderId", 1_000_000L);


        PaymentApiResponse response = new PaymentApiResponse("paymentKey", "orderId", 1_000L, "status");

        // When & Then
        assertAll(
                () -> assertThatThrownBy(() -> response.toEntity(request1)).isInstanceOf(PaymentException.class),
                () -> assertThatThrownBy(() -> response.toEntity(request2)).isInstanceOf(PaymentException.class),
                () -> assertThatThrownBy(() -> response.toEntity(request3)).isInstanceOf(PaymentException.class)
        );
    }
}
