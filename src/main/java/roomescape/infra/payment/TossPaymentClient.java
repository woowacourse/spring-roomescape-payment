package roomescape.infra.payment;

import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import roomescape.application.dto.request.payment.PaymentCancelRequest;
import roomescape.application.dto.request.payment.PaymentRequest;
import roomescape.application.dto.response.payment.PaymentResponse;
import roomescape.domain.payment.PaymentClient;
import roomescape.exception.payment.PaymentFailException;

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
    public PaymentResponse cancel(String paymentKey, PaymentCancelRequest request) {
        String encoded = Base64.getEncoder().encodeToString(secretKey.getBytes());

        try {
            return restClient.post()
                    .uri("/v1/payments/{paymentKey}/cancel", paymentKey)
                    .header("Authorization", "Basic " + encoded)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(PaymentResponse.class);
        } catch (HttpClientErrorException e) {
            PaymentErrorResult result = e.getResponseBodyAs(PaymentErrorResult.class);
            throw new PaymentFailException(result.message(), (HttpStatus) e.getStatusCode());
        }

    }
}
