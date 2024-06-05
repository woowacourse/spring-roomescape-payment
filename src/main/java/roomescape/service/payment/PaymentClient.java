package roomescape.service.payment;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.exception.payment.PaymentConfirmResponseErrorHandler;
import roomescape.service.payment.dto.PaymentConfirmInput;
import roomescape.service.payment.dto.PaymentConfirmOutput;

public class PaymentClient {
    private final RestClient restClient;

    public PaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentConfirmOutput confirmPayment(PaymentConfirmInput paymentConfirmInput) {
        return restClient.method(HttpMethod.POST)
                .uri("/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentConfirmInput)
                .retrieve()
                .onStatus(new PaymentConfirmResponseErrorHandler())
                .body(PaymentConfirmOutput.class);
    }
}
