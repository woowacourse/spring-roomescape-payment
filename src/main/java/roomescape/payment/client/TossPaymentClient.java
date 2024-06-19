package roomescape.payment.client;

import org.springframework.web.client.RestClient;
import roomescape.common.exception.PaymentException;
import roomescape.payment.dto.request.ConfirmPaymentRequest;
import roomescape.payment.dto.response.ConfirmPaymentResponse;
import roomescape.payment.model.Payment;
import roomescape.payment.model.PaymentStatus;

public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;

    public TossPaymentClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Payment confirm(ConfirmPaymentRequest confirmPaymentRequest) {
        ConfirmPaymentResponse confirmPaymentResponse = restClient.post()
                .uri("/confirm")
                .body(confirmPaymentRequest)
                .retrieve()
                .toEntity(ConfirmPaymentResponse.class)
                .getBody();

        if (confirmPaymentResponse.isCanceled()) {
            throw new PaymentException();
        }

        return new Payment(null, confirmPaymentResponse.paymentKey(), confirmPaymentResponse.orderId(),
                confirmPaymentResponse.totalAmount(), PaymentStatus.SUCCESS);
    }
}
