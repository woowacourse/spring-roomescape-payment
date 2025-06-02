package roomescape.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.config.RestClientConfiguration;
import roomescape.exception.custom.reason.payment.PaymentException;
import roomescape.payment.dto.PaymentConfirmRequest;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(TossPaymentClient.class)
@ContextConfiguration(classes = RestClientConfiguration.class)
class TossPaymentClientTest {

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private TossPaymentClient tossPaymentClient;

    @DisplayName("결제 승인 API의 응답 상태코드가 200이 아닌 경우 예외가 발생한다")
    @Test
    void confirm1() {
        // given
        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED));
        PaymentConfirmRequest paymentConfirmRequest = new PaymentConfirmRequest(
                "orderId",
                1000L,
                "paymentKey"
        );

        // when & then
        assertThatThrownBy(() -> tossPaymentClient.confirm(paymentConfirmRequest))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제 승인에 실패하였습니다.");
    }

    @DisplayName("결제 승인 API의 응답 에러 코드가 사용자에게 보여줘야 하는 경우 결제 실패 사유 메시지가 담긴 예외가 발생한다.")
    @Test
    void confirm2() {
        // given
        String expectedBody = """
                {
                  "code": "ALREADY_PROCESSED_PAYMENT",
                  "message": "이미 처리된 결제 입니다."
                }
                """;

        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .body(expectedBody)
                        .contentType(MediaType.APPLICATION_JSON));
        PaymentConfirmRequest paymentConfirmRequest = new PaymentConfirmRequest(
                "orderId",
                1000L,
                "paymentKey"
        );

        // when & then
        assertThatThrownBy(() -> tossPaymentClient.confirm(paymentConfirmRequest))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제 승인 실패: 이미 처리된 결제 입니다.");
    }

    @DisplayName("결제 승인 API의 응답 에러 코드가 사용자에게 보여주지 않아야 하는 경우 일괄적인 메시지가 담긴 예외가 발생한다.")
    @Test
    void confirm3() {
        // given
        String expectedBody = """
                {
                  "code": "INVALID_API_KEY",
                  "message": "잘못된 시크릿키 연동 정보 입니다."
                }
                """;

        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .body(expectedBody)
                        .contentType(MediaType.APPLICATION_JSON));
        PaymentConfirmRequest paymentConfirmRequest = new PaymentConfirmRequest(
                "orderId",
                1000L,
                "paymentKey"
        );

        // when & then
        assertThatThrownBy(() -> tossPaymentClient.confirm(paymentConfirmRequest))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제 승인에 실패하였습니다.");
    }

    @DisplayName("결제 승인 API의 응답 에러 코드가 예기치 못한 코드인 경우 일괄적인 메시지가 담긴 예외가 발생한다.")
    @Test
    void confirm4() {
        // given
        String expectedBody = """
                {
                  "code": "바보",
                  "message": "바보입니다."
                }
                """;

        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .body(expectedBody)
                        .contentType(MediaType.APPLICATION_JSON));
        PaymentConfirmRequest paymentConfirmRequest = new PaymentConfirmRequest(
                "orderId",
                1000L,
                "paymentKey"
        );

        // when & then
        assertThatThrownBy(() -> tossPaymentClient.confirm(paymentConfirmRequest))
                .isInstanceOf(PaymentException.class)
                .hasMessage("예상치 못한 오류로 인해 결제 승인에 실패하였습니다.");
    }

    @DisplayName("결제 승인 API 요청을 위한 서버 연결 시간이 초과된 경우 일괄적인 메시지가 담긴 예외가 발생한다.")
    @Test
    void confirm5() {
        // given
        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond((request) -> {
                    throw new ConnectException("Connection refused or timed out");
                });
        PaymentConfirmRequest paymentConfirmRequest = new PaymentConfirmRequest(
                "orderId",
                1000L,
                "paymentKey"
        );

        // when & then
        assertThatThrownBy(() -> tossPaymentClient.confirm(paymentConfirmRequest))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제 승인에 실패하였습니다.");
    }

    @DisplayName("결제 승인 API의 요청 시간이 초과된 경우 일괄적인 메시지가 담긴 예외가 발생한다.")
    @Test
    void confirm6() {
        // given
        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond((request) -> {
                    throw new SocketTimeoutException("read timeout occurred");
                });
        PaymentConfirmRequest paymentConfirmRequest = new PaymentConfirmRequest(
                "orderId",
                1000L,
                "paymentKey"
        );

        // when & then
        assertThatThrownBy(() -> tossPaymentClient.confirm(paymentConfirmRequest))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제 승인에 실패하였습니다.");
    }
}
