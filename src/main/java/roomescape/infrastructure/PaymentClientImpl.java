package roomescape.infrastructure;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import roomescape.core.dto.exception.HttpExceptionResponse;
import roomescape.core.dto.payment.PaymentCancelRequest;
import roomescape.core.dto.payment.PaymentCancelResponse;
import roomescape.core.dto.payment.PaymentConfirmRequest;
import roomescape.core.dto.payment.PaymentConfirmResponse;
import roomescape.core.dto.payment.PaymentRequest;
import roomescape.core.exception.PaymentException;

@Component
public class PaymentClientImpl implements PaymentClient {
    private final PaymentSecretKeyEncoder encoder;
    private final RestClient restClient;

    public PaymentClientImpl(final PaymentSecretKeyEncoder encoder, final RestClient restClient) {
        this.encoder = encoder;
        this.restClient = restClient;
    }

    @Override
    public PaymentConfirmResponse getPaymentConfirmResponse(final PaymentRequest memberRequest) {
        final String authorizations = encoder.getEncodedSecretKey();

        try {
            return getPaymentRequestResult(memberRequest, authorizations);
        } catch (final HttpClientErrorException exception) {
            final HttpStatusCode statusCode = exception.getStatusCode();
            final String statusText = exception.getStatusText();
            final HttpExceptionResponse responseBody = exception.getResponseBodyAs(HttpExceptionResponse.class);

            throw new PaymentException(statusCode, statusText, responseBody);
        }
    }

    private PaymentConfirmResponse getPaymentRequestResult(final PaymentRequest request, final String authorizations) {
        return restClient.post()
                .uri("/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorizations)
                .body(new PaymentConfirmRequest(request))
                .retrieve()
                .body(PaymentConfirmResponse.class);
    }

    @Override
    public PaymentCancelResponse getPaymentCancelResponse(final String paymentKey) {
        final String authorizations = encoder.getEncodedSecretKey();

        try {
            return getCancelRequestResult(paymentKey, authorizations);
        } catch (final HttpClientErrorException exception) {
            final HttpStatusCode statusCode = exception.getStatusCode();
            final String statusText = exception.getStatusText();
            final HttpExceptionResponse responseBody = exception.getResponseBodyAs(HttpExceptionResponse.class);

            throw new PaymentException(statusCode, statusText, responseBody);
        }
    }

    private PaymentCancelResponse getCancelRequestResult(final String paymentKey, final String authorizations) {
        return restClient.post()
                .uri("/{paymentKey}/cancel", paymentKey)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorizations)
                .body(new PaymentCancelRequest("단순 고객 변심"))
                .retrieve()
                .body(PaymentCancelResponse.class);
    }
}
