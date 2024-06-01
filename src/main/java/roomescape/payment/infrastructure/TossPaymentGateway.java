package roomescape.payment.infrastructure;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentConfirmResponse;

@Component
public class TossPaymentGateway implements PaymentGateway {

    private final RestClient restClient;

    public TossPaymentGateway(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public PaymentConfirmResponse confirm(
            final String orderId,
            final Long amount,
            final String paymentKey
    ) {
        PaymentConfirmRequest request = new PaymentConfirmRequest(orderId, amount, paymentKey);
        return restClient
                .post()
                .uri("/confirm")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .retrieve()
                .body(PaymentConfirmResponse.class);
    }
}
