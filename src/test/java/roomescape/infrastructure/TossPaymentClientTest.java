package roomescape.infrastructure;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.ResourceAccessException;
import roomescape.config.PaymentConfig;
import roomescape.domain.payment.PaymentClient;
import roomescape.exception.PaymentException;
import roomescape.service.dto.PaymentRequest;

@RestClientTest(value = PaymentClient.class)
@Import(PaymentConfig.class)
class TossPaymentClientTest {

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @DisplayName("결제 승인이 정상 처리된다.")
    @Test
    void success() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest(1000, "orderId", "paymentKey");

        mockRestServiceServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withSuccess());

        // when // then
        assertThatCode(() -> paymentClient.requestApproval(paymentRequest))
                .doesNotThrowAnyException();
    }

    @DisplayName("결제 승인 요청 시 인증이 실패되면 PaymentException 이 발생한다.")
    @Test
    void authorized() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest(1000, "orderId", "paymentKey");
        String errorResponse = """
                {
                  "code": "UNAUTHORIZED_KEY",
                  "message": "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."
                }
                """;

        mockRestServiceServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withUnauthorizedRequest()
                        .body(errorResponse)
                        .contentType(MediaType.APPLICATION_JSON));

        // when // then
        assertThatThrownBy(() -> paymentClient.requestApproval(paymentRequest))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제 서버 오류로 작업을 진행할 수 없습니다.");
    }

    @DisplayName("결제 승인 요청시 TimeOut되면 PaymentException이 발생한다.")
    @Test
    void timeOutException() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest(1000, "orderId", "paymentKey");

        mockRestServiceServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(request -> {
                    throw new ResourceAccessException("");
                });

        // when // then
        assertThatThrownBy(() -> paymentClient.requestApproval(paymentRequest))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제 서버와 연결이 되지 않습니다.");

    }
}
