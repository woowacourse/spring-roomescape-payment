package roomescape.payment.toss.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import roomescape.payment.toss.config.TestTossPaymentConfig;
import roomescape.payment.toss.dto.TossPaymentRequest;
import roomescape.payment.exception.PaymentProcessException;
import roomescape.payment.toss.interceptor.TossPaymentResponseInterceptor;

@ActiveProfiles("timeout")
@RestClientTest(TossPaymentClient.class)
@Import({TestTossPaymentConfig.class, TossPaymentResponseInterceptor.class})
public class TossPaymentTimeoutTest {

    private static final String PAYMENT_URL = "https://api.tosspayments.com/v1/payments";

    @Autowired
    protected MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestTossPaymentConfig testTossPaymentConfig;
    private TossPaymentClient tossPaymentClient;

    @PostConstruct
    void setUp() {

        RestClient.Builder builder = testTossPaymentConfig.restClientBuilder();
        mockServer = MockRestServiceServer.bindTo(builder).build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builder()
                .exchangeAdapter(RestClientAdapter.create(builder.build()))
                .build();

        tossPaymentClient = factory.createClient(TossPaymentClient.class);
    }

    @Test
    void 결제_서비스에_타임아웃이_발생한_경우_예외_반환() throws JsonProcessingException {

        String paymentKey = "paymentKey";
        String orderId = "orderId";
        Long amount = 10000L;

        TossPaymentRequest request = new TossPaymentRequest(paymentKey, orderId, amount);
        String json = objectMapper.writeValueAsString(request);

        mockServer.expect(requestTo(PAYMENT_URL + "/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(json))
                .andRespond(withStatus(HttpStatus.REQUEST_TIMEOUT)
                        .body("{\"code\":\"TIMEOUT\",\"message\":\"결제 서비스에 연결할 수 없습니다.\"}"));

        assertThatThrownBy(() -> tossPaymentClient.getPaymentConfirm(request))
                .isInstanceOf(PaymentProcessException.class)
                .hasMessage("결제 서비스에 연결할 수 없습니다.");
    }
}

