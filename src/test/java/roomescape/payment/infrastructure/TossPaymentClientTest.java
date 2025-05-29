package roomescape.payment.infrastructure;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.math.BigDecimal;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import roomescape.payment.application.dto.PaymentRequest;
import roomescape.payment.application.dto.PaymentResponse;

class TossPaymentClientTest {

    private MockWebServer mockWebServer;
    private TossPaymentClient tossPaymentClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        RestClient restClient = RestClient.builder()
                .baseUrl(mockWebServer.url("/8081").toString())
                .build();

        tossPaymentClient = new TossPaymentClient(restClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
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

        PaymentRequest request = new PaymentRequest(BigDecimal.valueOf(1000), "orderId", "paymentKey");

        // when
        PaymentResponse response = tossPaymentClient.requestPayment(request);

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

        PaymentRequest request = new PaymentRequest(BigDecimal.valueOf(1000), "orderId", "paymentKey");

        assertThatThrownBy(() -> tossPaymentClient.requestPayment(request))
                .isInstanceOf(TossPaymentException.class)
                .hasMessageContaining("잘못된 요청입니다.");
    }
}
