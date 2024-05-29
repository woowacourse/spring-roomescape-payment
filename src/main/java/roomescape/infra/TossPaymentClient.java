package roomescape.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.application.PaymentClient;
import roomescape.application.dto.request.PaymentRequest;
import roomescape.application.dto.response.PaymentErrorResponse;
import roomescape.application.dto.response.PaymentResponse;
import roomescape.exception.PaymentException;

@Component
public class TossPaymentClient implements PaymentClient {

    private final String encodedSecretKey;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public TossPaymentClient(
            @Value("${payment.secret-key}") String secretKey,
            RestClient restClient,
            ObjectMapper objectMapper
    ) {
        this.encodedSecretKey = encodeSecretKey(secretKey);
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public PaymentResponse confirmPayment(PaymentRequest paymentRequest) {
        return restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", encodedSecretKey)
                .body(paymentRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    PaymentErrorResponse errorResponse = objectMapper
                            .readValue(res.getBody(), PaymentErrorResponse.class);

                    throw new PaymentException(errorResponse.message());
                })
                .body(PaymentResponse.class);
    }

    private static String encodeSecretKey(String secretKey) {
        return "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }
}
