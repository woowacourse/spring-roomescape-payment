package roomescape.reservation;

import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;
import roomescape.reservation.dto.response.PaymentApproveResponse;
import roomescape.reservation.entity.Payment;

@RequiredArgsConstructor
public class PaymentRestClient {

    private static final String TEST_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:";
    private static final String ENCODED_KEY = Base64.getEncoder().encodeToString(TEST_SECRET_KEY.getBytes());

    private final RestClient restClient;

    public PaymentApproveResponse approve(Payment payment) {
        return restClient.post()
                .uri("/confirm")
                .header("Authorization", "Basic " + ENCODED_KEY)
                .body(payment)
                .retrieve()
                .body(PaymentApproveResponse.class);
    }
}
