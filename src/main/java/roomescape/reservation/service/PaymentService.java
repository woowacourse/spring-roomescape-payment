package roomescape.reservation.service;

import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.reservation.dto.request.PaymentConfirmRequest;

@Service
public class PaymentService {

    private final RestClient restClient;

    public PaymentService(RestClient restClient) {
        this.restClient = restClient;
    }

    public void confirmPayment(PaymentConfirmRequest request) {
        try {
            restClient.post()
                    .uri(new URI("/confirm"))
                    .body(request);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
