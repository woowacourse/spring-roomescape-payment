package roomescape.payment.infrastructure.toss.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.ExternalApiException;
import roomescape.payment.application.dto.PaymentApprovalRequest;
import roomescape.payment.infrastructure.toss.exception.TossErrorResponse;

@Component
public class TossPaymentClient {
    public static final String DEFAULT_ERROR_MESSAGE = "결제 승인에 오류가 발생하였습니다. 관리자에게 문의하세요";
    private static final String SERVICE_NAME = "Toss";
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
            throw new ExternalApiException(
                    SERVICE_NAME,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    errorResponse.code(),
                    DEFAULT_ERROR_MESSAGE
            );
        }
        throw new ExternalApiException(
                SERVICE_NAME,
                HttpStatus.BAD_REQUEST,
                errorResponse.code(),
                errorResponse.message()
        );
    }

    private void handle5xxError(ClientHttpResponse response) {
        TossErrorResponse errorResponse = parseErrorResponse(response);
        throw new ExternalApiException(
                SERVICE_NAME,
                HttpStatus.INTERNAL_SERVER_ERROR,
                errorResponse.code(),
                errorResponse.message()
        );
    }

    private TossErrorResponse parseErrorResponse(ClientHttpResponse response) {
        try {
            return objectMapper.readValue(response.getBody(), TossErrorResponse.class);
        } catch (Exception e) {
            throw new ExternalApiException(
                    SERVICE_NAME,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "PARSE_FAILED",
                    "Toss 응답 파싱 실패"
            );
        }
    }
}
