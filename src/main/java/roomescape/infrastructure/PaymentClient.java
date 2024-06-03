package roomescape.infrastructure;

import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.core.dto.auth.PaymentAuthorizationResponse;
import roomescape.core.dto.payment.PaymentRequest;
import roomescape.core.dto.payment.PaymentResponse;

@Component
public class PaymentClient {

    private final RestClient restClient;
    private final TossPaymentsProperties tossPaymentsProperties;

    public PaymentClient(final RestClient restClient, final TossPaymentsProperties tossPaymentsProperties) {
        this.restClient = restClient;
        this.tossPaymentsProperties = tossPaymentsProperties;
    }

    public void approvePayment(final PaymentRequest paymentRequest,
                               final PaymentAuthorizationResponse paymentAuthorizationResponse) {
        restClient.post()
                .uri(tossPaymentsProperties.getApi().getConfirmUrl())
                .header(HttpHeaders.AUTHORIZATION, paymentAuthorizationResponse.getPaymentAuthorization())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(paymentRequest)
                .retrieve()
                .onStatus(new PaymentApproveErrorHandler())
                .toBodilessEntity();
    }

    public void refundPayment(final PaymentResponse paymentResponse,
                              final PaymentAuthorizationResponse paymentAuthorizationResponse) {
        final String refundUrl = tossPaymentsProperties.getApi()
                .getRefundUrlTemplate()
                .replace("{paymentKey}", paymentResponse.getPaymentKey());

        restClient.post()
                .uri(refundUrl)
                .header(HttpHeaders.AUTHORIZATION, paymentAuthorizationResponse.getPaymentAuthorization())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Map.of("cancelReason", "고객 변심"))
                .retrieve()
                .onStatus(new PaymentRefundErrorHandler())
                .toBodilessEntity();
    }
}
