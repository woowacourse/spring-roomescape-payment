package roomescape.payment.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;
import roomescape.exception.PaymentConfirmFailException;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentConfirmResponse;
import roomescape.payment.dto.TossErrorResponse;

@Component
public class TossPaymentGateway implements PaymentGateway {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public TossPaymentGateway(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public PaymentConfirmResponse confirm(
            final String orderId,
            final Long amount,
            final String paymentKey
    ) {
        PaymentConfirmRequest confirmRequest = new PaymentConfirmRequest(orderId, amount, paymentKey);
        return restClient.post()
                .uri("/confirm")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(confirmRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, getErrorHandler())
                .body(PaymentConfirmResponse.class);
    }

    private ErrorHandler getErrorHandler() {
        return (request, response) -> {
            TossErrorResponse errorResponse = objectMapper.readValue(response.getBody(), TossErrorResponse.class);
            throw new PaymentConfirmFailException(errorResponse.message(), (HttpStatus) response.getStatusCode());
        };
    }
}
