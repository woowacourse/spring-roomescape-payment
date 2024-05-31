package roomescape.infrastructure;

import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.core.dto.auth.PaymentAuthorizationResponse;
import roomescape.core.dto.payment.TossPaymentRequest;
import roomescape.core.dto.payment.TossPaymentResponse;

@Component
public class PaymentClient {
    private final RestClient restClient;

    public PaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public TossPaymentResponse approvePayment(final TossPaymentRequest tossPaymentRequest,
                                              final PaymentAuthorizationResponse paymentAuthorizationResponse) {
        return restClient.post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
                .header("Authorization", paymentAuthorizationResponse.getPaymentAuthorization())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(tossPaymentRequest)
                .retrieve()
                .onStatus(new PaymentErrorHandler())
                .body(TossPaymentResponse.class);
    }

    public TossPaymentResponse refundPayment(final TossPaymentResponse tossPaymentResponse,
                                             final PaymentAuthorizationResponse paymentAuthorizationResponse) {
        return restClient.post()
                .uri("https://api.tosspayments.com/v1/payments/" + tossPaymentResponse.getPaymentKey() + "/cancel")
                .header("Authorization", paymentAuthorizationResponse.getPaymentAuthorization())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Map.of("cancelReason", "고객 변심"))
                .retrieve()
                .onStatus(new PaymentErrorHandler())
                .body(TossPaymentResponse.class);
    }
}
