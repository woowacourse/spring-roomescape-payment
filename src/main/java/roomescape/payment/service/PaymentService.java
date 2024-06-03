package roomescape.payment.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.request.PaymentConfirmRequest;

@Service
public class PaymentService {

    private final RestClient restClient;

    public PaymentService(RestClient.Builder paymentRestClientBuilder) {
        this.restClient = paymentRestClientBuilder.build();
    }

    // 결제 승인 api https://docs.tosspayments.com/reference#%EA%B2%B0%EC%A0%9C-%EC%8A%B9%EC%9D%B8
    public void confirmPayment(PaymentConfirmRequest confirmRequest) {
        restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(confirmRequest)
                .retrieve()
                .toBodilessEntity();
    }
}
