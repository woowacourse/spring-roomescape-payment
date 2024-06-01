package roomescape.client;

import org.springframework.web.client.RestClient;

public interface PaymentClientFactory {
    RestClient createPaymentClient(RestClient.Builder restClientBuilder);
}
