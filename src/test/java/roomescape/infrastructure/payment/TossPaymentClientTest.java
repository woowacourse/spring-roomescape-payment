package roomescape.infrastructure.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;
import roomescape.application.payment.config.PaymentClientProperties;
import roomescape.application.payment.config.PaymentClientProperty;
import roomescape.application.payment.config.PaymentRestClientBuilders;
import roomescape.application.payment.dto.PaymentClientRequest;
import roomescape.domain.payment.Payment;
import roomescape.exception.payment.PaymentException;
import roomescape.util.Base64Utils;

class TossPaymentClientTest {
    private final PaymentClientProperty property = new PaymentClientProperty(
            "test", "https://test-toss-url.com", "test-secret",
            Duration.ofSeconds(3L), Duration.ofSeconds(31L));
    private final PaymentRestClientBuilders builders = new PaymentRestClientBuilders(
            new PaymentClientProperties(List.of(property))
    );
    private final RestClient.Builder builder = builders.get("test")
            .defaultStatusHandler(new PaymentErrorHandler());
    private final MockRestServiceServer server = MockRestServiceServer.bindTo(builder)
            .bufferContent()
            .build();
    private final TossPaymentClient paymentClient = new TossPaymentClient(builder.build());

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
        server.expect(manyTimes(), requestTo(property.url() + "/v1/payments/confirm"))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "BASIC " + Base64Utils.encode(property.secret() + ":")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        PaymentClientRequest request = new PaymentClientRequest("1234abcd", 1000, "");
        Payment payment = paymentClient.requestPurchase(request);

        server.verify();
        assertAll(
                () -> assertThat(payment.getPaymentKey()).isEqualTo("qwer"),
                () -> assertThat(payment.getOrderId()).isEqualTo("1234abcd"),
                () -> assertThat(payment.getAmount()).isEqualTo(1000L)
        );
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
        server.expect(manyTimes(), requestTo(property.url() + "/v1/payments/confirm"))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "BASIC " + Base64Utils.encode(property.secret() + ":")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(request -> response);
        PaymentClientRequest request = new PaymentClientRequest("1234abcd", 1000, "");

        assertThatCode(() -> paymentClient.requestPurchase(request))
                .isInstanceOf(PaymentException.class)
                .hasMessageStartingWith("카드 정보를 다시 확인해주세요.");
        response.close();
    }

    @Test
    @DisplayName("ResponseBody가 비어있을 경우, 예외를 반환한다.")
    void errorOnEmptyResponseBody() {
        String body = "";
        MockClientHttpResponse response = new MockClientHttpResponse(
                body.getBytes(),
                HttpStatus.OK
        );
        server.expect(manyTimes(), requestTo(property.url() + "/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(request -> response);

        PaymentClientRequest request = new PaymentClientRequest("1234abcd", 1000, "");

        assertThatCode(() -> paymentClient.requestPurchase(request))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제에 실패했습니다.");
        response.close();
    }

    @Test
    @DisplayName("결제 서버에 요청 시간이 초과되면 예외를 변환해 반환한다.")
    void connectionTimeoutTest() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofNanos(1L))
                .withReadTimeout(Duration.ofSeconds(30L));
        JdkClientHttpRequestFactory factory = ClientHttpRequestFactories.get(
                JdkClientHttpRequestFactory.class, settings
        );

        Builder timeoutBuilder = builder.requestFactory(factory);
        TossPaymentClient timeoutClient = new TossPaymentClient(timeoutBuilder.build());

        PaymentClientRequest request = new PaymentClientRequest("1234abcd", 1000, "");
        server.expect(manyTimes(), requestTo(property.url() + "/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST));

        assertThatCode(() -> timeoutClient.requestPurchase(request))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제 서버 요청에 실패했습니다.");
    }

    @Test
    @DisplayName("결제 서버에서 응답 시간이 초과되면 예외를 변환해 반환한다.")
    void readTimeoutTest() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(3L))
                .withReadTimeout(Duration.ofNanos(1L));
        JdkClientHttpRequestFactory factory = ClientHttpRequestFactories.get(
                JdkClientHttpRequestFactory.class, settings
        );

        Builder timeoutBuilder = builder.requestFactory(factory);
        TossPaymentClient timeoutClient = new TossPaymentClient(timeoutBuilder.build());

        PaymentClientRequest request = new PaymentClientRequest("1234abcd", 1000, "");
        server.expect(manyTimes(), requestTo(property.url() + "/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST));

        assertThatCode(() -> timeoutClient.requestPurchase(request))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제 서버 요청에 실패했습니다.");
    }

    @Test
    @DisplayName("결제 완료가 아닌 경우, 결제에 실패했다는 예외를 반환한다.")
    void failPurchaseTest() {
        String body = """
                {
                    "orderId": "1234abcd",
                    "totalAmount": 1000,
                    "paymentKey": "qwer",
                    "status": "CANCELED"
                }
                """;
        server.expect(manyTimes(), requestTo(property.url() + "/v1/payments/confirm"))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "BASIC " + Base64Utils.encode(property.secret() + ":")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        PaymentClientRequest request = new PaymentClientRequest("1234abcd", 1000, "");
        assertThatCode(() -> paymentClient.requestPurchase(request))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제에 실패했습니다.");
    }

}
