package roomescape.payment.toss.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import roomescape.payment.toss.dto.TossPaymentRequest;

@ActiveProfiles("timeout")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class TossPaymentTimeoutTest {

    private static final int port = 6565;
    private final MockWebServer mockWebServer = new MockWebServer();
    @Autowired
    private ObjectMapper objectMapper;
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
    void 결제_서비스에_타임아웃이_발생한_경우_예외_반환1() throws JsonProcessingException {

        String paymentKey = "paymentKey";
        String orderId = "orderId";
        Long amount = 10000L;

        TossPaymentRequest request = new TossPaymentRequest(paymentKey, orderId, amount);
        String json = objectMapper.writeValueAsString(request);

        mockWebServer.enqueue(new MockResponse().setBodyDelay(10, TimeUnit.MILLISECONDS));

        assertThatThrownBy(() -> tossPaymentClient.getPaymentConfirm(request))
                .isInstanceOf(ResourceAccessException.class);
    }
}

