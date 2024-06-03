package roomescape.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;
import org.springframework.web.client.RestClientException;
import roomescape.exception.ErrorType;
import roomescape.exception.InternalException;
import roomescape.global.util.Authorization;
import roomescape.global.util.Encoder;
import roomescape.payment.TossPaymentProperties;
import roomescape.exception.PaymentException;
import roomescape.payment.exception.toss.TossPaymentErrorResponse;
import roomescape.payment.exception.toss.TossPaymentException;
import roomescape.payment.service.dto.PaymentRequest;
import roomescape.payment.service.dto.PaymentResponse;

@Component
public class PaymentClient {

    private final RestClient restClient;

    private final Encoder encoder;

    private final Authorization authorization;

    private final TossPaymentProperties tossPaymentProperties;

    private final ObjectMapper objectMapper;

    public PaymentClient(RestClient restClient,
                         Encoder encoder,
                         Authorization authorization,
                         TossPaymentProperties tossPaymentProperties,
                         ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.encoder = encoder;
        this.authorization = authorization;
        this.tossPaymentProperties = tossPaymentProperties;
        this.objectMapper = objectMapper;
    }

    public PaymentResponse confirm(PaymentRequest paymentRequest) {
        try {
            return restClient.post()
                    .uri(tossPaymentProperties.getConfirmUrl())
                    .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader())
                    .body(paymentRequest)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, handleError())
                    .body(PaymentResponse.class);
        } catch (RestClientException e) {
            handlerTimeoutPayment(paymentRequest.paymentKey(), e);
            throw new InternalException(ErrorType.NETWORK_ERROR);
        }
    }

    public PaymentResponse cancel(String paymentKey) {
        Map<String, String> params = new HashMap<>();
        String cancelReason = "단순 변심";
        params.put("cancelReason", cancelReason);
        try {
            return restClient.post()
                    .uri(String.format(tossPaymentProperties.getCancelUrl(), paymentKey))
                    .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader())
                    .body(params)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, handleError())
                    .body(PaymentResponse.class);
        } catch (RestClientException e) {
            handlerTimeoutPayment(paymentKey, e);
            throw new InternalException(ErrorType.NETWORK_ERROR);
        }
    }

    private void handlerTimeoutPayment(String paymentKey, RestClientException e) {
        if (e.getCause() instanceof SocketTimeoutException) {
            cancelPaymentIfConfirmed(paymentKey);
            throw new InternalException(ErrorType.PAYMENT_ERROR);
        }
    }

    private ErrorHandler handleError() {
        return (request, response) -> {
            throw new TossPaymentException(objectMapper.readValue(response.getBody(), TossPaymentErrorResponse.class));
        };
    }

    private String getAuthorizationHeader() {
        return authorization.getHeader(encoder.encode(tossPaymentProperties.getSecretKey()));
    }

    private void cancelPaymentIfConfirmed(String paymentKey) {
        if (isPaymentConfirmed(paymentKey)) {
            cancel(paymentKey);
        }
    }

    private boolean isPaymentConfirmed(String paymentKey) {
        PaymentResponse response = restClient.get()
                .uri(String.format(tossPaymentProperties.getCancelUrl(), paymentKey))
                .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader())
                .retrieve()
                .onStatus(HttpStatusCode::isError, handleError())
                .body(PaymentResponse.class);
        return Objects.equals(response.status(), "DONE");
    }
}
