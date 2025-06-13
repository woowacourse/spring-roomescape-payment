package roomescape.payment.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.global.exception.ClientTimeoutException.PaymentConnectionTimeoutException;
import roomescape.global.exception.ClientTimeoutException.PaymentReadTimeoutException;
import roomescape.payment.model.TossPaymentApprovalInfo;

class TossPaymentRestClientTest {

    private TossPaymentClientErrorHandler errorHandler = new TossPaymentClientErrorHandler(new ObjectMapper());
    private int connectionTimeoutSeconds = 1;
    private int readTimeoutSeconds = 1;

    @Test
    @DisplayName("1초 초과 Connection Timeout 시 PaymentConnectionTimeoutException을 던진다")
    void handleTimeoutError_ConnectionTimeout_3Seconds() {
        // given
        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:9999") // 열리지 않은 포트
                .requestFactory(createTimeoutRequestFactory(connectionTimeoutSeconds, readTimeoutSeconds)) // 3초 연결 타임아웃
                .build();

        TossPaymentRestClient client = new TossPaymentRestClient(restClient, errorHandler);
        TossPaymentApprovalInfo approveInfo = createApproveInfo();

        // when & then
        assertThatThrownBy(() -> client.requestApprove(approveInfo))
                .isInstanceOf(PaymentConnectionTimeoutException.class);
    }

    @Test
    @DisplayName("3초 이상 Read Timeout 시 PaymentReadTimeoutException을 던진다")
    void handleTimeoutError_ReadTimeout_30Seconds() throws IOException {
        // given
        MockWebServer server = new MockWebServer();
        server.start();

        server.enqueue(new MockResponse()
                .setBody("{\"status\": \"success\"}")
                .setHeadersDelay(readTimeoutSeconds + 1, TimeUnit.SECONDS));

        RestClient restClient = RestClient.builder()
                .baseUrl(server.url("/").toString())
                .requestFactory(createTimeoutRequestFactory(connectionTimeoutSeconds, readTimeoutSeconds))
                .build();

        TossPaymentRestClient client = new TossPaymentRestClient(restClient, errorHandler);
        TossPaymentApprovalInfo approveInfo = createApproveInfo();

        // when & then
        assertThatThrownBy(() -> client.requestApprove(approveInfo))
                .isInstanceOf(PaymentReadTimeoutException.class);

        server.shutdown();
    }

    private ClientHttpRequestFactory createTimeoutRequestFactory(int connectTimeoutSeconds, int readTimeoutSeconds) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(connectTimeoutSeconds));
        factory.setReadTimeout(Duration.ofSeconds(readTimeoutSeconds));
        return factory;
    }

    private TossPaymentApprovalInfo createApproveInfo() {
        return TossPaymentApprovalInfo.builder()
                .paymentKey("test-payment-key")
                .orderId("test-order-id")
                .amount(10000)
                .build();
    }
}
