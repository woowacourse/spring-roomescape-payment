package roomescape.payment.service;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.payment.service.dto.request.PaymentConfirmRequest;
import roomescape.payment.service.dto.resonse.PaymentConfirmResponse;

public class TossPaymentClient {

    private final RestClient restClient;

    public TossPaymentClient(RestClient.Builder paymentRestClientBuilder) {
        this.restClient = paymentRestClientBuilder.build();
    }

    // 결제 승인 api https://docs.tosspayments.com/reference#%EA%B2%B0%EC%A0%9C-%EC%8A%B9%EC%9D%B8
    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest confirmRequest) {
        return restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(confirmRequest)
                .retrieve()
                .body(PaymentConfirmResponse.class);
    }
}
