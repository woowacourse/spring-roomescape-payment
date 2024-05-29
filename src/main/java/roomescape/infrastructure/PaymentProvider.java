package roomescape.infrastructure;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.core.dto.payment.PaymentConfirmRequest;
import roomescape.core.dto.payment.PaymentConfirmResponse;
import roomescape.core.dto.reservation.ReservationPaymentRequest;

@Component
public class PaymentProvider {
    private final PaymentSecretKeyEncoder encoder;
    private final RestClient restClient;

    public PaymentProvider(final PaymentSecretKeyEncoder encoder, final RestClient restClient) {
        this.encoder = encoder;
        this.restClient = restClient;
    }

    public PaymentConfirmResponse getPaymentConfirmResponse(final ReservationPaymentRequest memberRequest) {
        final String authorizations = encoder.getEncodedSecretKey();

        return restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorizations)
                .body(new PaymentConfirmRequest(memberRequest))
                .retrieve()
                .body(PaymentConfirmResponse.class);
    }
}
