package roomescape.payment.infrastructure.client;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;
import roomescape.payment.model.PaymentClient;
import roomescape.reservation.model.vo.PaymentInfo;

@RequiredArgsConstructor
public class PaymentRestClient implements PaymentClient {
    
    private final RestClient restClient;

    @Override
    public void requestApprove(final PaymentInfo paymentInfo) {
        restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(APPLICATION_JSON)
                .body(paymentInfo)
                .retrieve()
                .toBodilessEntity();
    }
}
