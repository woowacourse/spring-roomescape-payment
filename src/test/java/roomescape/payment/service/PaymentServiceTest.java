package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.exception.PaymentException;
import roomescape.payment.exception.PaymentUnauthorizedException;

@RestClientTest(value = PaymentService.class)
class PaymentServiceTest {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @DisplayName("결제 확인 되었을 경우 정상 처리한다.")
    @Test
    void confirmPaymentTest() {
        PaymentConfirmRequest request = new PaymentConfirmRequest("testPaymentKey", "TestOrderId", 1000);

        mockRestServiceServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withSuccess());

        assertThatCode(() -> paymentService.confirmPayment(request)).doesNotThrowAnyException();
    }

    @DisplayName("결제 확인 오류가 발생한 경우 예외를 던진다.")
    @Test
    void confirmPaymentTest_whenNotConfirmed() {
        PaymentConfirmRequest request = new PaymentConfirmRequest("testPaymentKey", "TestOrderId", 1000);

        String errorResponse = """
                {
                  "code": "NOT_FOUND_PAYMENT",
                  "message": "존재하지 않는 결제 입니다."
                }
                """;
        mockRestServiceServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withBadRequest().body(errorResponse).contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> paymentService.confirmPayment(request))
                .isInstanceOf(PaymentException.class)
                .hasMessage("존재하지 않는 결제 입니다.");
    }

    @DisplayName("결제 확인시 인증오류 발생한 경우 예외를 던진다.")
    @Test
    void confirmPaymentTest_whenUnauthorized() {
        PaymentConfirmRequest request = new PaymentConfirmRequest("testPaymentKey", "TestOrderId", 1000);

        String errorResponse = """
                {
                  "code": "UNAUTHORIZED_KEY",
                  "message": "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."
                }
                """;
        mockRestServiceServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withUnauthorizedRequest().body(errorResponse).contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> paymentService.confirmPayment(request))
                .isInstanceOf(PaymentUnauthorizedException.class);
    }
}
