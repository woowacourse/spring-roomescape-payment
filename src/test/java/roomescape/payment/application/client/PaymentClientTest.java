package roomescape.payment.application.client;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import roomescape.payment.exception.PaymentClientException;
import roomescape.payment.exception.PaymentForbiddenException;
import roomescape.payment.exception.PaymentServerException;
import roomescape.payment.exception.PaymentUnauthorizedException;
import roomescape.payment.presentation.dto.request.PaymentApproveRequest;
import roomescape.payment.presentation.dto.response.TossErrorResponse;
import roomescape.payment.presentation.dto.response.TossPaymentApproveResponse;

@Import({PaymentClientConfig.class})
@RestClientTest(value = PaymentClient.class)
class PaymentClientTest {

    private static final String EXPECTED_RESULT = """
                {
                  "paymentKey": "tgen_20250528175227f6y46",
                  "orderId": "MC44NjE2MTQzMjcyMzM2",
                  "orderName": "토스 티셔츠 외 2건",
                  "secret": "ps_Z61JOxRQVEY4dQ1QR7ZwVW0X9bAq",
                  "type": "NORMAL",
                  "currency": "KRW",
                  "totalAmount": 50000,
                  "balanceAmount": 50000,
                  "suppliedAmount": 45455,
                  "vat": 4545,
                  "taxFreeAmount": 0,
                  "method": "간편결제",
                  "version": "2022-11-16",
                  "metadata": null
                }
            """;
    private static final String PAYMENT_KEY = "tgen_20250528175227f6y46";
    private static final String ORDER_ID = "MC44NjE2MTQzMjcyMzM2";

    @Autowired
    private PaymentClient paymentClient;

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
        PaymentApproveRequest request = new PaymentApproveRequest(PAYMENT_KEY, ORDER_ID,
                50_000L, null);

        // When
        TossPaymentApproveResponse response = paymentClient.approvePayment(request);

        // Then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(response).isNotNull();
            softAssertions.assertThat(response.paymentKey()).isEqualTo(PAYMENT_KEY);
            softAssertions.assertThat(response.orderId()).isEqualTo(ORDER_ID);
            softAssertions.assertThat(response.totalAmount()).isEqualTo(50000);
        });
    }

    @Test
    void approvePayment_whenUnauthorizedRequest_throwsException() throws JsonProcessingException {
        // Given
        PaymentApproveRequest request = new PaymentApproveRequest(PAYMENT_KEY, ORDER_ID,
                50_000L, null);
        String errorResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(new TossErrorResponse("UNAUTHORIZED_KEY", "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."));
        mockServer.expect(requestTo(url))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponse));

        // When
        assertThatThrownBy(() -> paymentClient.approvePayment(request))
                .isInstanceOf(PaymentUnauthorizedException.class);
    }

    @Test
    void approvePayment_whenForbiddenRequest_throwsException() throws JsonProcessingException {
        // Given
        PaymentApproveRequest request = new PaymentApproveRequest(PAYMENT_KEY, ORDER_ID,
                50_000L, null);
        String errorResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(new TossErrorResponse("REJECT_CARD_PAYMENT", "한도초과 혹은 잔액부족으로 결제에 실패했습니다."));
        mockServer.expect(requestTo(url))
                .andRespond(withStatus(HttpStatus.FORBIDDEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponse));

        // When
        assertThatThrownBy(() -> paymentClient.approvePayment(request))
                .isInstanceOf(PaymentForbiddenException.class);
    }

    @Test
    void approvePayment_whenInvalidClientRequest_throwsException() throws JsonProcessingException {
        // Given
        PaymentApproveRequest request = new PaymentApproveRequest(PAYMENT_KEY, ORDER_ID,
                50_000L, null);
        String errorResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(
                        new TossErrorResponse("NOT_FOUND_PAYMENT_SESSION", "결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다."));
        mockServer.expect(requestTo(url))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponse));

        // When
        assertThatThrownBy(() -> paymentClient.approvePayment(request))
                .isInstanceOf(PaymentClientException.class);
    }

    @Test
    void approvePayment_whenInvalidServerRequest_throwsException() throws JsonProcessingException {
        // Given
        PaymentApproveRequest request = new PaymentApproveRequest(PAYMENT_KEY, ORDER_ID,
                50_000L, null);
        String errorResponse = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(
                        new TossErrorResponse("UNKNOWN_PAYMENT_ERROR", "결제에 실패했어요. 같은 문제가 반복된다면 은행이나 카드사로 문의해주세요."));
        mockServer.expect(requestTo(url))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponse));

        // When
        assertThatThrownBy(() -> paymentClient.approvePayment(request))
                .isInstanceOf(PaymentServerException.class);
    }
}
