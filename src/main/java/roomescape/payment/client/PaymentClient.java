package roomescape.payment.client;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.request.ConfirmPaymentRequest;
import roomescape.payment.model.Payment;

@Component
public class PaymentClient {

    private final RestClient restClient;

    public PaymentClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    //TODO : 예외처리 추가
    public Payment confirm(@RequestBody ConfirmPaymentRequest confirmPaymentRequest) {
        return restClient.post()
                .uri("/confirm")
                .body(confirmPaymentRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new IllegalStateException();
                })
                .toEntity(Payment.class)
                .getBody();
    }
}
