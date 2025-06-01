package roomescape.payment.infrastructure.toss.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.application.dto.PaymentApprovalRequest;
import roomescape.payment.infrastructure.toss.exception.TossErrorResponse;
import roomescape.payment.infrastructure.toss.exception.TossInternalException;
import roomescape.payment.infrastructure.toss.exception.TossPaymentApprovalFailedException;

@Component
public class TossPaymentClient {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public TossPaymentClient(@Qualifier("tossApiRestClient") RestClient restClient,
                             ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public void approve(PaymentApprovalRequest approvalRequest) {
        restClient.post()
                .uri("/v1/payments/confirm")
                .body(approvalRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> handle4xxError(response))
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> handle5xxError(response))
                .toBodilessEntity();
    }

    private void handle4xxError(ClientHttpResponse response) {
        TossErrorResponse errorResponse = parseErrorResponse(response);
        if (errorResponse.hasNonUserFacingMessage()) {
            throw new TossInternalException();
        }
        throw new TossPaymentApprovalFailedException(
                HttpStatus.BAD_REQUEST,
                errorResponse.message()
        );
    }

    private void handle5xxError(ClientHttpResponse response) {
        TossErrorResponse errorResponse = parseErrorResponse(response);
        throw new TossPaymentApprovalFailedException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                errorResponse.message()
        );
    }

    private TossErrorResponse parseErrorResponse(ClientHttpResponse response) {
        try {
            return objectMapper.readValue(response.getBody(), TossErrorResponse.class);
        } catch (Exception e) {
            throw new TossInternalException();
        }
    }
}