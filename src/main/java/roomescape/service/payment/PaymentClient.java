package roomescape.service.payment;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import roomescape.service.payment.dto.PaymentConfirmInput;
import roomescape.service.payment.dto.PaymentConfirmOutput;

public class PaymentClient {
    private final RestClient restClient;
    private final ResponseErrorHandler errorHandler;

    public PaymentClient(RestClient restClient, ResponseErrorHandler errorHandler) {
        this.restClient = restClient;
        this.errorHandler = errorHandler;
    }

    public PaymentConfirmOutput confirmPayment(PaymentConfirmInput paymentConfirmInput) {
        return restClient.method(HttpMethod.POST)
                .uri("/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentConfirmInput)
                .retrieve()
                .onStatus(errorHandler)
                .body(PaymentConfirmOutput.class);
    }


}
