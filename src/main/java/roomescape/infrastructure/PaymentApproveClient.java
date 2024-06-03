package roomescape.infrastructure;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import roomescape.core.dto.auth.PaymentAuthorizationResponse;
import roomescape.core.dto.payment.PaymentRequest;

@Component
public class PaymentApproveClient {

    private final RestTemplate paymentApproveRestTemplate;
    private final TossPaymentsProperties tossPaymentsProperties;

    public PaymentApproveClient(final RestTemplate paymentApproveRestTemplate,
                                final TossPaymentsProperties tossPaymentsProperties) {
        this.paymentApproveRestTemplate = paymentApproveRestTemplate;
        this.tossPaymentsProperties = tossPaymentsProperties;
    }

    public ResponseEntity<Void> approvePayment(final PaymentRequest paymentRequest,
                                               final PaymentAuthorizationResponse paymentAuthorizationResponse) {
        return paymentApproveRestTemplate.postForEntity(
                tossPaymentsProperties.getApi().getConfirmUrl(),
                new HttpEntity<>(paymentRequest, getHttpHeaders(paymentAuthorizationResponse)),
                Void.class);
    }

    private HttpHeaders getHttpHeaders(PaymentAuthorizationResponse paymentAuthorizationResponse) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, paymentAuthorizationResponse.getPaymentAuthorization());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
