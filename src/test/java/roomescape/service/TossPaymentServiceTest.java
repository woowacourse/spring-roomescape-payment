package roomescape.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import roomescape.global.exception.RoomescapeException;
import roomescape.service.config.TossPaymentConfigProperties;

@EnableConfigurationProperties(value = TossPaymentConfigProperties.class)
@TestPropertySource("classpath:application.yml")
@RestClientTest
class TossPaymentServiceTest {

    private static final String TOSS_PAYMENTS_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String ORDER_ID = "dummy_order_id";
    private static final int AMOUNT = 1000;
    private static final String PAYMENT_KEY = "dummy_payment_key";

    private MockRestServiceServer server;

    private final RestTemplate restTemplate;
    private final TossPaymentService paymentService;

    public TossPaymentServiceTest(
        @Autowired RestTemplateBuilder builder,
        @Autowired TossPaymentConfigProperties properties
    ) {
        this.restTemplate = builder.build();
        this.paymentService = new TossPaymentService(properties, restTemplate);
    }

    @BeforeEach
    void setUpMockServer() {
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @DisplayName("성공: 정상 결제")
    @Test
    void pay_Success() {
        server.expect(requestTo(TOSS_PAYMENTS_URL))
            .andRespond(withSuccess());
        assertThatCode(() -> paymentService.pay(ORDER_ID, AMOUNT, PAYMENT_KEY))
            .doesNotThrowAnyException();
    }

    @DisplayName("실패: 결제 과정에서 400 에러 발생")
    @Test
    void pay_4xxError() {
        server.expect(requestTo(TOSS_PAYMENTS_URL))
            .andRespond(withBadRequest());
        assertThatThrownBy(() -> paymentService.pay(ORDER_ID, AMOUNT, PAYMENT_KEY))
            .isInstanceOf(RoomescapeException.class)
            .hasMessage("결제가 승인되지 않았습니다.");
    }

    @DisplayName("실패: 결제 과정에서 500 에러 발생")
    @Test
    void pay_5xxError() {
        server.expect(requestTo(TOSS_PAYMENTS_URL))
            .andRespond(withServerError());
        assertThatThrownBy(() -> paymentService.pay(ORDER_ID, AMOUNT, PAYMENT_KEY))
            .isInstanceOf(RoomescapeException.class)
            .hasMessage("결제가 승인되지 않았습니다.");
    }
}
