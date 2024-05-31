package roomescape.infra.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.application.dto.request.payment.PaymentRequest;
import roomescape.application.dto.response.payment.PaymentResponse;
import roomescape.domain.payment.CancelReason;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;
import roomescape.exception.payment.PaymentFailException;

@Component
@RequiredArgsConstructor
public class TossPaymentClient implements PaymentClient {
    private final RestClient restClient;

    @Override
    public PaymentResponse confirm(PaymentRequest paymentRequest) {
        try {
            return restClient.post()
                    .uri("/v1/payments/confirm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(paymentRequest)
                    .retrieve()
                    .body(PaymentResponse.class);
        } catch (Exception exception) {
            throw new PaymentFailException("결제에 실패했습니다", exception, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void cancel(Payment payment, CancelReason reason) {
        try {
            restClient.post()
                    .uri("/v1/payments/{paymentKey}/cancel", payment.getPaymentKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(reason)
                    .retrieve()
                    .body(PaymentResponse.class);
        } catch (Exception exception) {
            throw new PaymentFailException("결제에 실패했습니다", exception, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
