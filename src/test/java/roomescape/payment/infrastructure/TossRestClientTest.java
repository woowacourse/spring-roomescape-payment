package roomescape.payment.infrastructure;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.payment.PaymentFixture.ERROR_RESPONSE_BODY;
import static roomescape.payment.PaymentFixture.SUCCESS_RESPONSE_BODY;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.exception.PaymentTimeoutException;
import roomescape.payment.exception.TossPaymentException;

@SpringBootTest
@TestPropertySource(properties = "rest-client.toss-payment.base-url=http://localhost:8089")
class TossRestClientTest {

    @Autowired
    private TossRestClient tossRestClient;

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void setup() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void 결제_승인_정상_응답_확인() {
        // given
        wireMockServer.stubFor(post(urlEqualTo("/v1/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(SUCCESS_RESPONSE_BODY)
                ));

        String paymentKey = "test_key";
        String orderId = "a4CWyWY5m89PNh7xJwhk1";
        Long amount = 1000L;
        TossPaymentRequest request = new TossPaymentRequest(paymentKey, orderId, amount);

        // when
        TossPaymentResponse response = tossRestClient.confirm(request);

        // then
        SoftAssertions.assertSoftly(soft -> {
            assertThat(response.paymentKey()).isEqualTo("test_key");
            assertThat(response.status()).isEqualTo("DONE");
        });
    }

    @Test
    void 결제_승인_잘못된_key_예외_처리() {
        // given
        wireMockServer.stubFor(post(urlEqualTo("/v1/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(ERROR_RESPONSE_BODY)
                ));

        String paymentKey = "wrong_test_key";
        String orderId = "a4CWyWY5m89PNh7xJwhk1";
        Long amount = 1000L;
        TossPaymentRequest request = new TossPaymentRequest(paymentKey, orderId, amount);

        // when
        // then
        assertThatThrownBy(() -> tossRestClient.confirm(request))
                .isInstanceOf(TossPaymentException.class)
                .hasMessageContaining("잘못된 시크릿키 연동 정보 입니다.");
    }

    @Test
    void 타임아웃_시간내_응답_시_정상_응답_확인() {
        // given
        wireMockServer.stubFor(post(urlEqualTo("/v1/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(SUCCESS_RESPONSE_BODY)
                        .withFixedDelay(4_000)
                ));


        String paymentKey = "test_key";
        String orderId = "a4CWyWY5m89PNh7xJwhk1";
        Long amount = 1000L;
        TossPaymentRequest request = new TossPaymentRequest(paymentKey, orderId, amount);

        // when
        TossPaymentResponse response = tossRestClient.confirm(request);

        // then
        SoftAssertions.assertSoftly(soft -> {
            assertThat(response.paymentKey()).isEqualTo("test_key");
            assertThat(response.status()).isEqualTo("DONE");
        });
    }

    @Test
    void 타임아웃_시간외_응답_시_예외_처리() {
        // given
        wireMockServer.stubFor(post(urlEqualTo("/v1/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(SUCCESS_RESPONSE_BODY)
                        .withFixedDelay(5_000)
                ));


        String paymentKey = "test_key";
        String orderId = "a4CWyWY5m89PNh7xJwhk1";
        Long amount = 1000L;
        TossPaymentRequest request = new TossPaymentRequest(paymentKey, orderId, amount);

        // when
        // then
        assertThatThrownBy(() -> tossRestClient.confirm(request))
                .isInstanceOf(PaymentTimeoutException.class);
    }
}
