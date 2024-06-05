package roomescape.infrastructure;

import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import roomescape.core.dto.auth.PaymentAuthorizationResponse;
import roomescape.core.dto.payment.PaymentResponse;

@Component
public class PaymentRefundClient {

    private static final Map<String, String> CANCEL_REASON = Map.of("cancelReason", "고객 변심");

    private final RestTemplate paymentRefundRestTemplate;
    private final TossPaymentsProperties tossPaymentsProperties;

    public PaymentRefundClient(final RestTemplate paymentRefundRestTemplate,
                               final TossPaymentsProperties tossPaymentsProperties) {
        this.paymentRefundRestTemplate = paymentRefundRestTemplate;
        this.tossPaymentsProperties = tossPaymentsProperties;
    }

    public void refundPayment(final PaymentResponse paymentResponse,
                              final PaymentAuthorizationResponse paymentAuthorizationResponse) {
        String requestUrl = tossPaymentsProperties.getApi().getRefundUrlTemplate()
                .replace("{paymentKey}", paymentResponse.getPaymentKey());

        paymentRefundRestTemplate.postForEntity(
                requestUrl,
                new HttpEntity<>(CANCEL_REASON, getHttpHeaders(paymentAuthorizationResponse)),
                Void.class);
    }

    private HttpHeaders getHttpHeaders(PaymentAuthorizationResponse paymentAuthorizationResponse) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, paymentAuthorizationResponse.getPaymentAuthorization());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
