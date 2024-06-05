package roomescape.infrastructure;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;
import roomescape.core.dto.auth.PaymentAuthorizationResponse;
import roomescape.core.dto.payment.TossPaymentRequest;
import roomescape.core.dto.payment.TossPaymentResponse;

@RestClientTest(PaymentClient.class)
class PaymentClientTest {
    private static final String WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private static final String tossApiUrl = "https://api.tosspayments.com/";

    private final Builder builder = RestClient.builder();
    private final MockRestServiceServer mockServer = MockRestServiceServer.bindTo(builder)
            .build();
    private final PaymentClient paymentClient = new PaymentClient(builder
            .baseUrl("https://api.tosspayments.com/")
            .build());

    @Test
    @DisplayName("Toss Api 가 PaymentResponse 를 올바르게 반환한다.")
    void testTossPaymentApi() {
        String body = """
                {
                "paymentKey": "mockPaymentKey",
                "orderId": "mockOrderId",
                "amount": 1000
                }
                """;

        String encode = Base64.getEncoder().encodeToString((WIDGET_SECRET_KEY + ":").getBytes());
        mockServer.expect(requestTo(tossApiUrl + "v1/payments/confirm"))
                .andExpect(header(HttpHeaders.AUTHORIZATION,
                        "Basic " + encode))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        TossPaymentResponse response = paymentClient.approvePayment(
                new TossPaymentRequest("mockPaymentKey", "mockOrderId", 1000L),
                new PaymentAuthorizationResponse("Basic " + encode));
        assertEquals("mockPaymentKey", response.getPaymentKey());

        mockServer.verify();
    }
}
