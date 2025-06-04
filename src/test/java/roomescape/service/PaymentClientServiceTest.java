package roomescape.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.SocketTimeoutException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import roomescape.dto.payment.PaymentConfirmRequest;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PaymentClientServiceTest {

    private static MockWebServer mockWebServer;

    @Autowired
    private PaymentClientService paymentClientService;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @DynamicPropertySource
    static void overrideBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("toss.base-url", () -> mockWebServer.url("/").toString());
    }

    @DisplayName("토스 결제 승인 요청이 타임아웃됐을 때 예외를 발생한다.")
    @Test
    void confirmPaymentTimeout_shouldThrowTossPaymentException() {
        mockWebServer.enqueue(new MockResponse()
                .setSocketPolicy(SocketPolicy.NO_RESPONSE));

        // when & then
        var paymentConfirmRequest = new PaymentConfirmRequest("orderId", 50000, "paymentKey", "TOSS");
        assertThatThrownBy(() -> paymentClientService.confirm(paymentConfirmRequest))
                .hasCauseInstanceOf(SocketTimeoutException.class);
    }
}