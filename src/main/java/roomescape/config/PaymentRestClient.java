package roomescape.config;

import org.springframework.web.client.RestClient;
import roomescape.domain.Payment;

public class PaymentRestClient {

    private final RestClient restClient;

    public PaymentRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public Payment getPayment() {
        return restClient.get()
                .retrieve()
                .body(Payment.class);
    }
}
