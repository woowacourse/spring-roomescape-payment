package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.ResourceAccessException;
import roomescape.payment.client.TossPaymentClient;
import roomescape.payment.dto.TossPaymentRequest;

@ActiveProfiles("timeout")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class TossPaymentTimeoutTest {

    private static final int port = 6565;
    private final MockWebServer mockWebServer = new MockWebServer();

    @Autowired
    private TossPaymentClient tossPaymentClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer.start(port);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void 결제_서비스에_타임아웃이_발생한_경우_예외_반환() {
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        Long amount = 10000L;

        TossPaymentRequest request = new TossPaymentRequest(paymentKey, orderId, amount);

        mockWebServer.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeadersDelay(200, TimeUnit.MILLISECONDS));

        assertThatThrownBy(() -> tossPaymentClient.getPaymentConfirm(request))
                .isInstanceOf(ResourceAccessException.class);
    }

    @Test
    void 결제_서비스에_타임아웃_정상_테스트() {
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        Long amount = 10000L;

        TossPaymentRequest request = new TossPaymentRequest(paymentKey, orderId, amount);

        mockWebServer.enqueue(new MockResponse().setBodyDelay(1, TimeUnit.MILLISECONDS));

        assertThatCode(() -> tossPaymentClient.getPaymentConfirm(request))
                .doesNotThrowAnyException();
    }
}

