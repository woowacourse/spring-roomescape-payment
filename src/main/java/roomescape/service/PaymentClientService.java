package roomescape.service;

import org.springframework.web.client.RestClient;

public class PaymentClientService {
    private final RestClient restClient;

    public PaymentClientService(final RestClient restClient) {
        this.restClient = restClient;
    }
}
