package roomescape.infra.payment;

import java.util.Base64;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final PaymentApiResponseErrorHandler errorHandler;

    @Value("${payments.secret-key}:")
    private String secretKey;

    @Override
    public PaymentResponse confirm(PaymentRequest paymentRequest) {
        String encoded = Base64.getEncoder().encodeToString(secretKey.getBytes());

        return Optional.ofNullable(restClient.post()
                        .uri("/v1/payments/confirm")
                        .header("Authorization", "Basic " + encoded)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(paymentRequest)
                        .retrieve()
                        .onStatus(errorHandler)
                        .body(PaymentResponse.class))
                .orElseThrow(() -> new PaymentFailException("결제 실패", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Override
    public void cancel(Payment payment, CancelReason reason) {
        String encoded = Base64.getEncoder().encodeToString(secretKey.getBytes());

        Optional.ofNullable(restClient.post()
                        .uri("/v1/payments/{paymentKey}/cancel", payment.getPaymentKey())
                        .header("Authorization", "Basic " + encoded)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(reason)
                        .retrieve()
                        .onStatus(errorHandler)
                        .body(PaymentResponse.class))
                .orElseThrow(() -> new PaymentFailException("결제 실패", HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
