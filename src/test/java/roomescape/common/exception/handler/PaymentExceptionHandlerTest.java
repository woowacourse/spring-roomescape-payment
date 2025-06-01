package roomescape.common.exception.handler;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import roomescape.common.config.TestPaymentConfig;
import roomescape.common.exception.PaymentClientException;
import roomescape.common.exception.PaymentServerException;
import roomescape.dto.request.TossPaymentConfirmDto;
import roomescape.infrastructure.payment.toss.TossPaymentWithRestClient;

@RestClientTest(value = TossPaymentWithRestClient.class)
@Import(TestPaymentConfig.class)
class PaymentExceptionHandlerTest {

    @Autowired
    MockRestServiceServer server;

    @Autowired
    TossPaymentWithRestClient tossPaymentWithRestClient;

    @DisplayName("토스의 클라이언트 에러를 내부의 에러로 변환해 서버 에러를 발생시킨다")
    @Test
    void test1() {
        // given
        String errorCode = "UNAUTHORIZED_KEY";
        String errorResponseBody = """
                    {
                      "code": "UNAUTHORIZED_KEY",
                      "message": "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."
                    }
                """.formatted(errorCode);

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST)).andExpect(content().json("""
                        {
                               "paymentKey": "sample-key",
                               "orderId": "order-123",
                               "amount": 10000
                        }
                        """)).andRespond(
                        withStatus(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON)
                                .body(errorResponseBody));

        // when & then
        TossPaymentConfirmDto requestDto = new TossPaymentConfirmDto("sample-key", "order-123",
                10000L);
        assertThatThrownBy(
                () -> tossPaymentWithRestClient.requestConfirmation(requestDto)).isInstanceOf(
                PaymentServerException.class);

    }

    @DisplayName("토스의 클라이언트 에러를 내부의 에러로 변환해 클라이언트 에러를 발생시킨다")
    @Test
    void test2() {
        // given
        String errorCode = "ALREADY_PROCESSED_PAYMENT";
        String errorResponseBody = """
                    {
                      "code": "ALREADY_PROCESSED_PAYMENT",
                      "message": "이미 처리된 결제 입니다."
                    }
                """.formatted(errorCode);

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST)).andExpect(content().json("""
                        {
                               "paymentKey": "sample-key",
                               "orderId": "order-123",
                               "amount": 10000
                           }
                        """)).andRespond(
                        withStatus(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                                .body(errorResponseBody));

        // when & then
        TossPaymentConfirmDto requestDto = new TossPaymentConfirmDto("sample-key", "order-123",
                10000L);
        assertThatThrownBy(
                () -> tossPaymentWithRestClient.requestConfirmation(requestDto)).isInstanceOf(
                PaymentClientException.class);

    }

    @DisplayName("토스에 지정되지 않은 예외를 PaymentServerException 으로 감싸 던진다")
    @Test
    void test3() {
        // given
        String errorCode = "NEW_ADDED_ERROR";
        String errorResponseBody = """
                    {
                      "code": "NEW_ADDED_ERROR",
                      "message": "새로 정의된 에러."
                    }
                """.formatted(errorCode);

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST)).andExpect(content().json("""
                        {
                               "paymentKey": "sample-key",
                               "orderId": "order-123",
                               "amount": 10000
                           }
                        """)).andRespond(
                        withStatus(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                                .body(errorResponseBody));

        // when & then
        TossPaymentConfirmDto requestDto = new TossPaymentConfirmDto("sample-key", "order-123",
                10000L);
        assertThatThrownBy(
                () -> tossPaymentWithRestClient.requestConfirmation(requestDto)).isInstanceOf(
                PaymentServerException.class);

    }
}
