package roomescape.reservation.service;

import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;
import roomescape.reservation.dto.response.PaymentApproveResponse;
import roomescape.reservation.entity.Payment;

@RequiredArgsConstructor
public class PaymentRestClient {

    private final RestClient restClient;
    private final String secretKey;

    public PaymentApproveResponse approve(Payment payment) {
        return restClient.post()
                .uri("/confirm")
                .header("Authorization", "Basic " + getEncodedKey())
                .body(payment)
                .retrieve()
                .body(PaymentApproveResponse.class);
    }

    private String getEncodedKey() {
        return Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
}
