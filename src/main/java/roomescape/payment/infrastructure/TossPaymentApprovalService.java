package roomescape.payment.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import roomescape.payment.application.PaymentApprovalService;
import roomescape.payment.infrastructure.client.TossRestClient;
import roomescape.payment.infrastructure.exception.TossErrorResponse;
import roomescape.payment.infrastructure.exception.TossInternalException;
import roomescape.payment.infrastructure.exception.TossPaymentApprovalFailedException;

//TODO: 에러 핸들링 하기!!  (2025-05-28, 수, 15:5)
@Component
@AllArgsConstructor
public class TossPaymentApprovalService implements PaymentApprovalService {

    private final TossRestClient tossRestClient;
    private final ObjectMapper objectMapper;

    @Override
    public void approvePayment(String orderId, BigDecimal amount, String paymentKey) {
        Map<String, Object> body = Map.of(
                "orderId", orderId,
                "amount", amount,
                "paymentKey", paymentKey
        );

        tossRestClient.getRestClient().post()
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
