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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.exception.PaymentTimeoutException;
import roomescape.payment.exception.TossPaymentException;

@SpringBootTest
@TestPropertySource(properties = "toss-payment.base-url=http://localhost:8089")
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

    @DisplayName("결제 승인 정상 처리")
    @Test
    void confirmSuccess() {
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

    @DisplayName("결제 승인 실패 클라이언트 에러(4xx 에러코드)")
    @Test
    void confirmError_wrongSecretKey() {
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

        // when & then
        assertThatThrownBy(() -> tossRestClient.confirm(request))
                .isInstanceOf(TossPaymentException.class)
                .hasMessageContaining("잘못된 시크릿키 연동 정보 입니다.");
    }

    @Disabled //: 타임아웃 포함 테스트 필요 시 Disbabled 제거 가능
    @DisplayName("타임아웃 시간 내 응답 시 정상 처리")
    @Test
    void confirmSuccess_beforeTimeout() {
        // given
        wireMockServer.stubFor(post(urlEqualTo("/v1/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(SUCCESS_RESPONSE_BODY)
                        .withFixedDelay(1_000) // 테스트 타임아웃 설정시간 2초
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

    @Disabled //: 타임아웃 포함 테스트 필요 시 Disbabled 제거 가능
    @DisplayName("타임아웃 시간 초과 시 예외 처리")
    @Test
    void confirmError_timeout() {
        // given
        wireMockServer.stubFor(post(urlEqualTo("/v1/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(SUCCESS_RESPONSE_BODY)
                        .withFixedDelay(2_000) // 테스트 타임아웃 설정시간 2초
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

    @DisplayName("타임아웃 외 네트워크 오류 시 예외 그대로 전파")
    @Test
    void confirmError_resourceAccessException() {
        // given
        RestClient brokenRestClient = RestClient.builder()
                .baseUrl("http://localhost:9999") // 잘못된 포트
                .build();
        TossRestClient brokenTossClient = new TossRestClient(brokenRestClient,
                new TossPaymentProperties(
                        "secret-key",
                        "https://api.tosspayments.com",
                        1000,
                        2000,
                        1000
                )
        );

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
        // then
        assertThatThrownBy(() -> brokenTossClient.confirm(request))
                .isInstanceOf(ResourceAccessException.class);
    }

    @DisplayName("에러 응답 파싱 실패시 예외 처리")
    @Test
    void confirmError_errorResponseParsingFail() {
        // given
        String invalidFormattedErrorResponse = """
                {
                    잘못된JSON형식
                }
                """;

        wireMockServer.stubFor(post(urlEqualTo("/v1/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(invalidFormattedErrorResponse)
                ));

        String paymentKey = "test_key";
        String orderId = "a4CWyWY5m89PNh7xJwhk1";
        Long amount = 1000L;
        TossPaymentRequest request = new TossPaymentRequest(paymentKey, orderId, amount);

        // when
        // then
        assertThatThrownBy(() -> tossRestClient.confirm(request))
                .isInstanceOf(TossPaymentException.class)
                .hasMessage("토스 오류 응답을 파싱할 수 없습니다.");
    }
}
