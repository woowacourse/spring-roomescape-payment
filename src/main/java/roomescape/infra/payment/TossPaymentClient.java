package roomescape.infra.payment;

import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
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

    @Value("${payments.secret-key}:")
    private String secretKey;

    @Override
    public PaymentResponse confirm(PaymentRequest paymentRequest) {
        String encoded = Base64.getEncoder().encodeToString(secretKey.getBytes());

        try {
            return restClient.post()
                    .uri("/v1/payments/confirm")
                    .header("Authorization", "Basic " + encoded)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(paymentRequest)
                    .retrieve()
                    .body(PaymentResponse.class);
        } catch (HttpClientErrorException e) {
            PaymentErrorResult result = e.getResponseBodyAs(PaymentErrorResult.class);
            throw new PaymentFailException(result.message(), (HttpStatus) e.getStatusCode());
        }
    }

    @Override
    public void cancel(Payment payment, CancelReason reason) {
        String encoded = Base64.getEncoder().encodeToString(secretKey.getBytes());

        try {
            restClient.post()
                    .uri("/v1/payments/{paymentKey}/cancel", payment.getPaymentKey())
                    .header("Authorization", "Basic " + encoded)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(reason)
                    .retrieve()
                    .body(PaymentResponse.class);
        } catch (HttpClientErrorException e) {
            PaymentErrorResult result = e.getResponseBodyAs(PaymentErrorResult.class);
            throw new PaymentFailException(result.message(), (HttpStatus) e.getStatusCode());
        }
    }
}
