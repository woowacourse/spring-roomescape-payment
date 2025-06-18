package roomescape.payment.application.client;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.common.config.PaymentClientConfig;
import roomescape.common.properties.PaymentClientProperties;
import roomescape.payment.application.dto.PaymentGatewayRequest;
import roomescape.payment.application.dto.PaymentGatewayResponse;
import roomescape.payment.domain.PaymentGateway;
import roomescape.payment.domain.PaymentType;
import roomescape.payment.exception.PaymentClientException;
import roomescape.payment.exception.PaymentForbiddenException;
import roomescape.payment.exception.PaymentServerException;
import roomescape.payment.exception.TossUnrecoverableErrorCode;
import roomescape.payment.presentation.dto.response.TossErrorResponse;

@Import({PaymentClientConfig.class})
@RestClientTest(value = TossPaymentGateway.class)
class PaymentClientTest {

    private static final String EXPECTED_RESULT = """
                {
                  "paymentKey": "tgen_20250528175227f6y46",
                  "orderId": "MC44NjE2MTQzMjcyMzM2",
                  "secret": "ps_Z61JOxRQVEY4dQ1QR7ZwVW0X9bAq",
                  "type": "NORMAL",
                  "totalAmount": 50000
                }
            """;
    private static final String PAYMENT_KEY = "tgen_20250528175227f6y46";
    private static final String ORDER_ID = "MC44NjE2MTQzMjcyMzM2";
    private static final String IDEMPOTENCY_KEY = UUID.randomUUID().toString();

    @Autowired
    private PaymentGateway paymentGateway;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private PaymentClientProperties paymentClientProperties;

    @Autowired
    private ObjectMapper objectMapper;

    private String url;

    @BeforeEach
    void setUp() {
        url = paymentClientProperties.getBaseUrl() + paymentClientProperties.getConfirmApi();
    }

    @Test
    void approvePayment_whenValidRequest_returnSuccessfully() {
        // Given
        mockServer.expect(requestTo(url))
                .andRespond(withSuccess(EXPECTED_RESULT, MediaType.APPLICATION_JSON));
        PaymentGatewayRequest request = new PaymentGatewayRequest(PAYMENT_KEY, ORDER_ID,
                50_000L, null, PaymentType.NORMAL, IDEMPOTENCY_KEY);

        // When
        PaymentGatewayResponse response = paymentGateway.approvePayment(request);

        // Then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(response).isNotNull();
            softAssertions.assertThat(response.paymentKey()).isEqualTo(PAYMENT_KEY);
            softAssertions.assertThat(response.orderId()).isEqualTo(ORDER_ID);
            softAssertions.assertThat(response.amount()).isEqualTo(50000);
        });
    }

    @Test
    void approvePayment_whenForbiddenRequest_throwsExceptionWithOurMessage()
            throws JsonProcessingException {
        // Given
        PaymentGatewayRequest request = new PaymentGatewayRequest(PAYMENT_KEY, ORDER_ID,
                50_000L, null, PaymentType.NORMAL, IDEMPOTENCY_KEY);
        String errorResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(new TossErrorResponse("REJECT_CARD_PAYMENT", "한도초과"));
        mockServer.expect(times(3), requestTo(url))
                .andRespond(withStatus(HttpStatus.FORBIDDEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponse));

        // When
        assertThatThrownBy(() -> paymentGateway.approvePayment(request))
                .isInstanceOf(PaymentForbiddenException.class)
                .hasMessageContaining("한도초과 혹은 잔액부족으로 결제에 실패했습니다.");
    }

    @Test
    void approvePayment_whenInvalidClientRequest_throwsException() throws JsonProcessingException {
        // Given
        PaymentGatewayRequest request = new PaymentGatewayRequest(PAYMENT_KEY, ORDER_ID,
                50_000L, null, PaymentType.NORMAL, IDEMPOTENCY_KEY);
        String errorResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(
                        new TossErrorResponse("", "신용카드는 결제금액이 100원 이상, 계좌는 200원이상부터 결제가 가능합니다."));
        mockServer.expect(times(3), requestTo(url))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponse));

        // When
        assertThatThrownBy(() -> paymentGateway.approvePayment(request))
                .isInstanceOf(PaymentClientException.class);
    }

    @Test
    void approvePayment_whenInvalidServerRequest_throwsException() throws JsonProcessingException {
        // Given
        PaymentGatewayRequest request = new PaymentGatewayRequest(PAYMENT_KEY, ORDER_ID,
                50_000L, null, PaymentType.NORMAL, IDEMPOTENCY_KEY);
        String errorResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(
                        new TossErrorResponse("UNKNOWN_PAYMENT_ERROR", "결제에 실패했어요. 같은 문제가 반복된다면 은행이나 카드사로 문의해주세요."));
        mockServer.expect(requestTo(url))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponse));

        // When
        assertThatThrownBy(() -> paymentGateway.approvePayment(request))
                .isInstanceOf(PaymentServerException.class);
    }

    @Test
    void approvePayment_whenUnrecoverableRequest_throwsException() throws JsonProcessingException {
        // Given
        PaymentGatewayRequest request = new PaymentGatewayRequest(PAYMENT_KEY, ORDER_ID, 50_000L, null,
                PaymentType.NORMAL, IDEMPOTENCY_KEY);
        TossUnrecoverableErrorCode errorCode = TossUnrecoverableErrorCode.INCORRECT_BASIC_AUTH_FORMAT;
        String errorResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(new TossErrorResponse(errorCode.name(), errorCode.getDescription()));
        mockServer.expect(requestTo(url))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponse));

        // When
        assertThatThrownBy(() -> paymentGateway.approvePayment(request))
                .isInstanceOf(PaymentServerException.class);
    }
}
