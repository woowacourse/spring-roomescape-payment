package roomescape.payment.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.PaymentException;
import roomescape.payment.client.dto.request.TossPaymentConfirmRequest;
import roomescape.payment.client.dto.response.TossErrorResponse;
import roomescape.payment.client.dto.response.TossPaymentResponse;

@Component
public class TossPaymentClient {

    private final RestClient tossRestClient;
    private final ObjectMapper objectMapper;

    public TossPaymentClient(RestClient tossRestClient, ObjectMapper objectMapper) {
        this.tossRestClient = tossRestClient;
        this.objectMapper = objectMapper;
    }

    public TossPaymentResponse confirmPayment(TossPaymentConfirmRequest request) {
        return tossRestClient.post()
                .uri("/payments/confirm")
                .body(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        (req, res) -> {
                            String errorBody = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                            TossErrorResponse errorResponse = objectMapper.readValue(errorBody, TossErrorResponse.class);
                            HttpStatusCode statusCode = res.getStatusCode();
                            throw new PaymentException(statusCode, "결제 실패 : " + errorResponse.message());
                        }
                )
                .body(TossPaymentResponse.class);
    }
}
