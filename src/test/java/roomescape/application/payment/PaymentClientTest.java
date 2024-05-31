package roomescape.application.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.application.config.PaymentClientConfig;
import roomescape.application.config.PaymentClientProperties;
import roomescape.application.payment.dto.Payment;
import roomescape.application.payment.dto.request.PaymentRequest;
import roomescape.exception.payment.PaymentException;

@RestClientTest(PaymentClientConfig.class)
class PaymentClientTest {

    @Autowired
    private PaymentClientProperties properties;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private PaymentClient paymentClient;

    @AfterEach
    void resetMockServer() {
        server.reset();
    }

    @Test
    @DisplayName("Payment 객체를 올바르게 반환한다.")
    void payment() {
        String body = """
                    {
                        "orderId": "1234abcd",
                        "totalAmount": 1000,
                        "paymentKey": "qwer",
                        "status": "DONE"
                    }
                """;
        server.expect(manyTimes(), requestTo(properties.getUrl() + "/v1/payments/confirm"))
                .andExpect(header("Authorization", properties.getBasicKey()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        PaymentRequest request = new PaymentRequest("1234abcd", 1000, "");
        Payment payment = paymentClient.requestPurchase(request);

        assertThat(payment)
                .isEqualTo(new Payment("qwer", "1234abcd", "DONE", 1000L));
    }

    @Test
    @DisplayName("외부 서버가 오류를 반환하면 예외를 반환한다.")
    void errorResponse() {
        String body = """
                {
                    "code": "INVALID_CARD_EXPIRATION",
                    "message":"카드 정보를 다시 확인해주세요.\\n(유효기간)"
                }
                """;
        MockClientHttpResponse response = new MockClientHttpResponse(
                body.getBytes(),
                HttpStatus.BAD_REQUEST
        );
        server.expect(manyTimes(), requestTo(properties.getUrl() + "/v1/payments/confirm"))
                .andExpect(header("Authorization", properties.getBasicKey()))
                .andExpect(method(HttpMethod.POST))
                .andRespond((req) -> response);
        PaymentRequest request = new PaymentRequest("1234abcd", 1000, "");

        assertThatCode(() -> paymentClient.requestPurchase(request))
                .isInstanceOf(PaymentException.class)
                .hasMessageStartingWith("카드 정보를 다시 확인해주세요.");
    }

    @Test
    @DisplayName("ResponseBody가 비어있을 경우, 예외를 반환한다.")
    void errorOnEmptyResponseBody() {
        String body = "";
        MockClientHttpResponse response = new MockClientHttpResponse(
                body.getBytes(),
                HttpStatus.OK
        );
        server.expect(manyTimes(), requestTo(properties.getUrl() + "/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond((req) -> response);

        PaymentRequest request = new PaymentRequest("1234abcd", 1000, "");

        assertThatCode(() -> paymentClient.requestPurchase(request))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제에 실패했습니다.");
    }
}
