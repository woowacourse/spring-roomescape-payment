package roomescape.application.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import roomescape.application.payment.dto.Payment;
import roomescape.application.payment.dto.request.PaymentRequest;

@Component
public class PaymentClient {

    private final RestClient client;
    private final ResponseErrorHandler handler;
    private final String url;

    public PaymentClient(RestClient.Builder builder,
                         ResponseErrorHandler handler,
                         @Value("${payment.url}") String url) {
        this.client = builder.build();
        this.handler = handler;
        this.url = url;
    }

    public Payment requestPurchase(String secret, PaymentRequest request) {
        return client.post()
                .uri(url)
                .header("Authorization", secret)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(handler)
                .body(Payment.class);
    }
}
