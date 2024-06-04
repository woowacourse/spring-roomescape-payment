package roomescape.client;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import roomescape.config.ClientConfig;
import roomescape.reservation.dto.request.PaymentRequest;

@Import(value = ClientConfig.class)
@SpringBootTest
class PaymentRestClientTest {
    @Autowired
    private PaymentRestClient paymentClient;

    @Test
    @DisplayName("서드 파티 API에 결제 요청을 한다.")
    void payForReservation_ShouldRequestToThirdPartAPI() {
        assertThatCode(() -> paymentClient.payForReservation("authorization", new PaymentRequest(1000, "", "")))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("서드 파티 API 요청이 실패하면 예외를 발생한다.")
    void payForReservation_ShouldThrowException_WhenAPICallFailed() {
        assertThatThrownBy(() -> paymentClient.payForReservation("", new PaymentRequest(1000, "", "")))
                .isInstanceOf(PaymentException.class);
    }
}
