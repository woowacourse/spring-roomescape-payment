package roomescape.infrastructure;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import roomescape.core.dto.exception.HttpExceptionResponse;
import roomescape.core.dto.payment.PaymentConfirmRequest;
import roomescape.core.dto.payment.PaymentConfirmResponse;
import roomescape.core.dto.reservation.ReservationPaymentRequest;
import roomescape.core.exception.PaymentException;

@Component
public class PaymentClientImpl implements PaymentClient {
    private final PaymentSecretKeyEncoder encoder;
    private final RestClient restClient;

    public PaymentClientImpl(final PaymentSecretKeyEncoder encoder, final RestClient restClient) {
        this.encoder = encoder;
        this.restClient = restClient;
    }

    public PaymentConfirmResponse getPaymentConfirmResponse(final ReservationPaymentRequest memberRequest) {
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

    private PaymentConfirmResponse getPaymentRequestResult(final ReservationPaymentRequest memberRequest,
                                                           final String authorizations) {
        return restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorizations)
                .body(new PaymentConfirmRequest(memberRequest))
                .retrieve()
                .body(PaymentConfirmResponse.class);
    }
}
