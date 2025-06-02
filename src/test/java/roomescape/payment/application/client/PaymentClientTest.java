package roomescape.payment.application.client;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.assertj.core.api.Assertions;
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
import roomescape.payment.exception.PaymentApproveException;
import roomescape.payment.presentation.dto.request.PaymentApproveRequest;
import roomescape.payment.presentation.dto.response.PaymentApproveResponse;

@Import({PaymentClientConfig.class})
@RestClientTest(value = PaymentClient.class)
class PaymentClientTest {

    private static final String EXPECTED_RESULT = """
                {
                  "paymentKey": "tgen_20250528175227f6y46",
                  "orderId": "MC44NjE2MTQzMjcyMzM2",
                  "totalAmount": 50000
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


    @BeforeEach
    void setUp() {
        String baseUrl = paymentClientProperties.getBaseUrl();
        mockServer.expect(requestTo(baseUrl + paymentClientProperties.getConfirmApi()))
                .andRespond(withSuccess(EXPECTED_RESULT, MediaType.APPLICATION_JSON));
    }

    @Test
    void 결제_승인_요청을_보내고_응답을_파싱할_수_있다() {
        // Given
        PaymentApproveRequest request = new PaymentApproveRequest(PAYMENT_KEY, ORDER_ID,
                50_000L);

        // When
        PaymentApproveResponse response = paymentClient.approvePayment(request);

        // Then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(response).isNotNull();
            softAssertions.assertThat(response.paymentKey()).isEqualTo(PAYMENT_KEY);
            softAssertions.assertThat(response.orderId()).isEqualTo(ORDER_ID);
            softAssertions.assertThat(response.totalAmount()).isEqualTo(50000);
        });
    }

    @Test
    void 잘못된_시크릿_키로_요청할_경우_예외가_발생한다() {
        // Given
        PaymentApproveRequest request = new PaymentApproveRequest("INVALID", ORDER_ID,
                50_000L);
        mockServer.reset();
        mockServer.expect(requestTo(paymentClientProperties.getBaseUrl() + paymentClientProperties.getConfirmApi()))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        // When & Then
        assertThatThrownBy(() -> paymentClient.approvePayment(request))
                .isInstanceOf(PaymentApproveException.class);
    }
}
