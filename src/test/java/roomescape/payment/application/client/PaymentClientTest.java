package roomescape.payment.application.client;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.common.config.PaymentClientConfig;
import roomescape.common.properties.PaymentClientProperties;
import roomescape.payment.presentation.dto.request.PaymentApproveRequest;
import roomescape.payment.presentation.dto.response.PaymentApproveResponse;

@Import({PaymentClientConfig.class})
@RestClientTest(value = PaymentClient.class)
class PaymentClientTest {

    private static final String EXPECTED_RESULT = """
                {
                  "mId": "tgen_docs",
                  "lastTransactionKey": "txrd_a01jwb1h3jba2a1gnrqv9cr0f45",
                  "paymentKey": "tgen_20250528175227f6y46",
                  "orderId": "MC44NjE2MTQzMjcyMzM2",
                  "orderName": "토스 티셔츠 외 2건",
                  "taxExemptionAmount": 0,
                  "status": "DONE",
                  "requestedAt": "2025-05-28T17:52:27+09:00",
                  "approvedAt": "2025-05-28T17:53:17+09:00",
                  "useEscrow": false,
                  "cultureExpense": false,
                  "card": null,
                  "virtualAccount": null,
                  "transfer": null,
                  "mobilePhone": null,
                  "giftCertificate": null,
                  "cashReceipt": null,
                  "cashReceipts": null,
                  "discount": null,
                  "cancels": null,
                  "secret": "ps_Z61JOxRQVEY4dQ1QR7ZwVW0X9bAq",
                  "type": "NORMAL",
                  "easyPay": {
                    "provider": "토스페이",
                    "amount": 50000,
                    "discountAmount": 0
                  },
                  "country": "KR",
                  "failure": null,
                  "isPartialCancelable": true,
                  "receipt": {
                    "url": "https://dashboard.tosspayments.com/receipt/redirection?transactionId=tgen_20250528175227f6y46&ref=PX"
                  },
                  "checkout": {
                    "url": "https://api.tosspayments.com/v1/payments/tgen_20250528175227f6y46/checkout"
                  },
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

    @BeforeEach
    void setUp() {
        String baseUrl = paymentClientProperties.getBaseUrl();
        String expectedResult = EXPECTED_RESULT;
        mockServer.expect(requestTo(baseUrl + paymentClientProperties.getConfirmApi()))
                .andRespond(withSuccess(expectedResult, MediaType.APPLICATION_JSON));
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
}
