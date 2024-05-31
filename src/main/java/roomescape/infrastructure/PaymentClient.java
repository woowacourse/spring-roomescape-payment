package roomescape.infrastructure;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${toss.payments.api.confirm-url}")
    private String confirmUrl;
    @Value("${toss.payments.api.refund-url-template}")
    private String refundUrlTemplate;

    public PaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public void approvePayment(
            PaymentRequest paymentRequest,
            PaymentAuthorizationResponse paymentAuthorizationResponse
    ) {
        restClient.post()
                .uri(confirmUrl)
                .header(HttpHeaders.AUTHORIZATION, paymentAuthorizationResponse.getPaymentAuthorization())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(paymentRequest)
                .retrieve()
                .onStatus(new PaymentErrorHandler())
                .toBodilessEntity();
    }

    public void refundPayment(
            PaymentResponse paymentResponse,
            PaymentAuthorizationResponse paymentAuthorizationResponse
    ) {
        String refundUrl = refundUrlTemplate.replace("{paymentKey}", paymentResponse.getPaymentKey());

        restClient.post()
                .uri(refundUrl)
                .header("Authorization", paymentAuthorizationResponse.getPaymentAuthorization())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Map.of("cancelReason", "고객 변심"))
                .retrieve()
                .onStatus(new PaymentErrorHandler())
                .toBodilessEntity();
    }
}
