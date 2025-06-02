package roomescape.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.client.dto.request.TossPaymentConfirmRequest;
import roomescape.client.dto.response.TossErrorResponse;
import roomescape.client.dto.response.TossPaymentResponse;
import roomescape.common.exception.PaymentException;

@Component
public class TossPaymentClient {

    private final RestClient tossRestClient;

    public TossPaymentClient(RestClient tossRestClient) {
        this.tossRestClient = tossRestClient;
    }

    public TossPaymentResponse confirmPayment(TossPaymentConfirmRequest request) {
        return tossRestClient.post()
                .uri("/payments/confirm")
                .body(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        (req, res) -> {
                            handleTossPaymentException(res);
                        }
                )
                .body(TossPaymentResponse.class);
    }

    private void handleTossPaymentException(ClientHttpResponse res) throws IOException {
        try {
            String errorBody = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            TossErrorResponse errorResponse = mapper.readValue(errorBody, TossErrorResponse.class);
            HttpStatusCode statusCode = res.getStatusCode();
            throw new PaymentException(statusCode, "결제 실패 : " + errorResponse.message());
        } catch (Exception e) {
            throw new PaymentException(res.getStatusCode(), "결제 실패 : " + e.getMessage());
        }
    }
}
