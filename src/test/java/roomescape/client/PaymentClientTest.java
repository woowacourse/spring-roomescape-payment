package roomescape.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatusCode;

import roomescape.client.fake.FakeHeaderConstant;
import roomescape.config.ClientConfig;
import roomescape.reservation.dto.request.PaymentRequest;

@Import(ClientConfig.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class PaymentClientTest {

    @Autowired
    private PaymentClient paymentClient;

    @Test
    @DisplayName("결제 요청을 한다.")
    void confirm_payment_request() {
        // given
        PaymentRequest requestBody = new PaymentRequest(100L, "orderId", "paymentKey");
        String authorizationHeaderValue = FakeHeaderConstant.AUTHORIZATION_HEADER.getValue();

        // when
        HttpStatusCode statusCode = paymentClient.confirm(authorizationHeaderValue, requestBody);

        // then
        assertThat(statusCode.is2xxSuccessful()).isTrue();
    }

    @Test
    @DisplayName("잘못된 인증헤더 값일 시 예외를 발생한다.")
    void confirm_should_throw_exception_when_invalid_auth_header() {
        // given
        PaymentRequest requestBody = new PaymentRequest(100L, "", "");
        String authorizationHeaderValue = "wrongauthorizationnheader";

        // when & then
        assertThatThrownBy(() -> paymentClient.confirm(authorizationHeaderValue, requestBody))
                .isInstanceOf(PaymentException.class);
    }
}
