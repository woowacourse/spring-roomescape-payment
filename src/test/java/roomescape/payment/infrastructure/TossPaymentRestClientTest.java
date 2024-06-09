package roomescape.payment.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;
import roomescape.Fixtures;
import roomescape.exception.PaymentFailureException;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("토스페이 클라이언트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TossPaymentRestClientTest {

    private final TossPaymentRestClient tossPaymentRestClient;
    private RestClient restClient;

    @Autowired
    public TossPaymentRestClientTest(TossPaymentRestClient tossPaymentRestClient) {
        this.tossPaymentRestClient = tossPaymentRestClient;
    }

    @BeforeEach
    void setUp() {
        this.restClient = tossPaymentRestClient.getRestClient();
    }

    @DisplayName("토스페이 클라이언트는 결제 사용자의 잘못인 경우 예외가 발생한다.")
    @Test
    void confirmWithUserError() {
        // given
        PaymentRequest paymentRequest = Fixtures.paymentRequestFixture;

        // when & then
        assertThatThrownBy(() -> restClient.post()
                .uri("/confirm")
                .header("Tosspayments-Test-Code", "INVALID_STOPPED_CARD")
                .body(paymentRequest)
                .retrieve()
                .body(PaymentResponse.class))
                .isInstanceOf(PaymentFailureException.class)
                .hasMessage("정지된 카드 입니다.");
    }

    @DisplayName("토스페이 클라이언트는 클라이언트의 잘못인 경우 커스텀된 메시지로 예외가 발생한다.")
    @Test
    void confirmWithClientError() {
        // given
        String code = "INVALID_API_KEY";
        PaymentRequest paymentRequest = Fixtures.paymentRequestFixture;

        // when & then
        assertThatThrownBy(() -> restClient.post()
                .uri("/confirm")
                .header("Tosspayments-Test-Code", code)
                .body(paymentRequest)
                .retrieve()
                .body(PaymentResponse.class))
                .isInstanceOf(PaymentFailureException.class)
                .hasMessage("결제를 진행하던 중 서버에 오류가 발생했습니다.");
    }
}
