package roomescape.infra.payment;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import roomescape.application.dto.request.payment.PaymentRequest;
import roomescape.application.dto.response.payment.PaymentResponse;
import roomescape.domain.payment.PaymentClient;
import roomescape.exception.PaymentServerException;

@Component
@RequiredArgsConstructor
public class TossPaymentClient implements PaymentClient {
    private static final String PAYMENTS_CONFIRM_URI = "/v1/payments/confirm";
    private static final String AUTHORIZATION_PREFIX = "Basic ";

    private final PaymentApiResponseErrorHandler errorHandler;
    private final PaymentSecretKey secretKey;
    private final RestClient restClient;

    @Override
    public PaymentResponse confirmPayment(PaymentRequest paymentRequest) {
        try {
            return Optional.ofNullable(restClient.post()
                            .uri(PAYMENTS_CONFIRM_URI)
                            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_PREFIX + secretKey.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(paymentRequest)
                            .retrieve()
                            .onStatus(errorHandler)
                            .body(PaymentResponse.class))
                    .orElse(PaymentResponse.empty());
        } catch (RestClientException e) {
            throw new PaymentServerException(e.getMessage(), e.getCause());
        }
    }
}
