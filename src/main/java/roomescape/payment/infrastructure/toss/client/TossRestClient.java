package roomescape.payment.infrastructure.toss.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.infrastructure.toss.exception.TossErrorResponse;
import roomescape.payment.infrastructure.toss.exception.TossInternalException;
import roomescape.payment.infrastructure.toss.exception.TossPaymentApprovalFailedException;

@Component
public class TossRestClient {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public TossRestClient(@Qualifier("tossApiRestClient") RestClient restClient,
                          ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public void approve(String orderId, BigDecimal amount, String paymentKey) {
        Map<String, Object> body = Map.of(
                "orderId", orderId,
                "amount", amount,
                "paymentKey", paymentKey
        );

        restClient.post()
                .uri("/v1/payments/confirm")
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        (request, response) -> {
                            TossErrorResponse errorResponse = objectMapper.readValue(response.getBody(),
                                    TossErrorResponse.class);
                            handleError(errorResponse);
                        })
                .onStatus(HttpStatusCode::is5xxServerError,
                        (request, response) -> {
                            TossErrorResponse errorResponse = objectMapper.readValue(response.getBody(),
                                    TossErrorResponse.class);
                            throw new TossPaymentApprovalFailedException(HttpStatus.INTERNAL_SERVER_ERROR,
                                    errorResponse.message());
                        })
                .toBodilessEntity();
    }

    private void handleError(TossErrorResponse errorResponse) {
        if (errorResponse.hasNonUserFacingMessage()) {
            throw new TossInternalException();
        }
        throw new TossPaymentApprovalFailedException(HttpStatus.BAD_REQUEST, errorResponse.message());
    }
}
