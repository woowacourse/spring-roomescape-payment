package roomescape.infrastructure;

import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.core.controller.PaymentErrorHandler;
import roomescape.core.dto.auth.PaymentAuthorizationResponse;
import roomescape.core.dto.payment.PaymentRequest;
import roomescape.core.dto.payment.PaymentResponse;

@Component
public class PaymentClient {
    private final RestClient restClient;

    public PaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentResponse approvePayment(final PaymentRequest paymentRequest,
                                          final PaymentAuthorizationResponse paymentAuthorizationResponse) {
        return restClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", paymentAuthorizationResponse.getPaymentAuthorization())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(paymentRequest)
                .retrieve()
                .onStatus(new PaymentErrorHandler())
                .body(PaymentResponse.class);
    }

    public PaymentResponse refundPayment(final PaymentResponse paymentResponse,
                                         final PaymentAuthorizationResponse paymentAuthorizationResponse) {
        return restClient.post()
                .uri("/v1/payments/" + paymentResponse.getPaymentKey() + "/cancel")
                .header("Authorization", paymentAuthorizationResponse.getPaymentAuthorization())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Map.of("cancelReason", "고객 변심"))
                .retrieve()
                .onStatus(new PaymentErrorHandler())
                .body(PaymentResponse.class);
    }
}
