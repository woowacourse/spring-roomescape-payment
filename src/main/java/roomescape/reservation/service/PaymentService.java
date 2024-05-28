package roomescape.reservation.service;

import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec;
import roomescape.reservation.dto.request.PaymentConfirmRequest;

@Service
public class PaymentService {

    private final RestClient restClient;

    public PaymentService(RestClient restClient) {
        this.restClient = restClient;
    }

    public void confirmPayment(PaymentConfirmRequest paymentConfirmRequest) {
        try {
            ResponseSpec responseSpec = restClient.post()
                    .uri(new URI("https://api.tosspayments.com/v1/payments/confirm"))
                    .body(paymentConfirmRequest)
                    .retrieve()
                    .onStatus(status -> status.value() == 200, (request, response) -> {
                        System.out.println("request = " + request);
                        System.out.println("response = " + response);
                    });
            System.out.println("responseSpec = " + responseSpec);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
