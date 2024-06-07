package roomescape.infrastructure.payment;

import static roomescape.infrastructure.payment.toss.TossPaymentErrorCode.SERVER_ERROR;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import roomescape.application.dto.request.payment.PaymentRequest;
import roomescape.application.dto.response.payment.PaymentResponse;
import roomescape.domain.payment.CancelReason;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;
import roomescape.exception.payment.PaymentFailException;

@RequiredArgsConstructor
public class TossPaymentClient implements PaymentClient {
    private final RestClient restClient;
    private final ResponseErrorHandler responseErrorHandler;

    @Override
    public PaymentResponse confirm(PaymentRequest paymentRequest) {
        try {
            return restClient.post()
                    .uri("/v1/payments/confirm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(paymentRequest)
                    .retrieve()
                    .onStatus(responseErrorHandler)
                    .body(PaymentResponse.class);
        } catch (PaymentFailException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new PaymentFailException(SERVER_ERROR.getMessage(), exception, HttpStatus.INTERNAL_SERVER_ERROR);
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
                    .onStatus(responseErrorHandler)
                    .body(PaymentResponse.class);
        } catch (PaymentFailException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new PaymentFailException(SERVER_ERROR.getMessage(), exception, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
