package roomescape.payment.infrastructure;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import roomescape.payment.TestRestClientConfig;
import roomescape.payment.application.dto.DefaultPaymentRequest;
import roomescape.payment.application.dto.PaymentRequest;
import roomescape.payment.application.dto.PaymentResponse;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(TestRestClientConfig.class)
class TossPaymentClientTest {

    @Autowired
    private MockWebServer mockWebServer;

    @Autowired
    private RestClient restClient;

    @Autowired
    private ObjectMapper objectMapper;

    private TossPaymentClient tossPaymentClient;

    @BeforeEach
    void setUp() {
        tossPaymentClient = new TossPaymentClient(restClient, objectMapper);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown(); // destroyMethod 있으니까 없어도 됨
    }

    @Test
    void 결제요청_성공() {
        // given
        String expectedResponse = """
                {
                  "orderId": "orderId",
                  "paymentKey": "paymentKey",
                  "totalAmount": 1000
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(expectedResponse)
                .addHeader("Content-Type", "application/json"));

        PaymentRequest request = new DefaultPaymentRequest("paymentKey", "orderId", BigDecimal.valueOf(1000));

        // when
        PaymentResponse response = tossPaymentClient.requestPaymentConfirm(request);

        // then
        assertThat(response.orderId()).isEqualTo("orderId");
        assertThat(response.paymentKey()).isEqualTo("paymentKey");
        assertThat(response.totalAmount()).isEqualTo(1000);
    }

    @Test
    void 결제요청_실패하면_TossPaymentException_던짐() {
        String errorJson = """
                {
                  "code": "INVALID_REQUEST",
                  "message": "잘못된 요청입니다."
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody(errorJson)
                .addHeader("Content-Type", "application/json"));

        PaymentRequest request = new DefaultPaymentRequest("paymentKey", "orderId", BigDecimal.valueOf(1000));

        assertThatThrownBy(() -> tossPaymentClient.requestPaymentConfirm(request))
                .isInstanceOf(TossPaymentException.class)
                .hasMessageContaining("잘못된 요청입니다.");
    }

    @Test
    void 이미_처리된_결제는_ALREDY_PROCESSED_PAYMENT_에러를_던진다() {
        String errorJson = """
                {
                  "code": "ALREADY_PROCESSED_PAYMENT",
                  "message": "이미 처리된 결제 입니다."
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody(errorJson)
                .addHeader("Content-Type", "application/json"));

        PaymentRequest request = new DefaultPaymentRequest("paymentKey", "orderId", BigDecimal.valueOf(1000));

        assertThatThrownBy(() -> tossPaymentClient.requestPaymentConfirm(request))
                .isInstanceOf(TossPaymentException.class)
                .hasMessageContaining("이미 처리된 결제");
    }

    @Test
    void 토스_장애시_PROVIDER_ERROR_에러를_던진다() {
        String errorJson = """
                {
                  "code": "PROVIDER_ERROR",
                  "message": "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody(errorJson)
                .addHeader("Content-Type", "application/json"));

        PaymentRequest request = new DefaultPaymentRequest("paymentKey", "orderId", BigDecimal.valueOf(1000));

        assertThatThrownBy(() -> tossPaymentClient.requestPaymentConfirm(request))
                .isInstanceOf(TossPaymentException.class)
                .hasMessageContaining("일시적인 오류");
    }

    @Test
    void 응답시간이_타임아웃을_초과하면_예외를_던짐() {
        // given: 5초 뒤에 응답 오게 설정
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("""
                        {
                          "orderId": "orderId",
                          "paymentKey": "paymentKey",
                          "totalAmount": 1000
                        }
                        """)
                .setBodyDelay(5, TimeUnit.SECONDS) // ★ 여기서 응답 지연
                .addHeader("Content-Type", "application/json"));

        PaymentRequest request = new DefaultPaymentRequest("paymentKey", "orderId", BigDecimal.valueOf(1000));

        // when & then
        assertThatThrownBy(() -> tossPaymentClient.requestPaymentConfirm(request))
                .hasCauseInstanceOf(SocketTimeoutException.class)
                .isInstanceOf(RestClientException.class);
    }


}
