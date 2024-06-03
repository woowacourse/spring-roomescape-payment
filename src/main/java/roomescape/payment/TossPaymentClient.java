package roomescape.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import roomescape.global.exception.PaymentFailException;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentConfirmResponse;
import roomescape.payment.dto.TossErrorResponse;

public class TossPaymentClient {

    private static final String WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private static final String KEY_DELIMITER = ":";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final RestClient restClient;

    public TossPaymentClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentConfirmResponse confirmPayments(PaymentConfirmRequest request) {
        String secretKey = WIDGET_SECRET_KEY + KEY_DELIMITER;

        return restClient.post().uri("/v1/payments/confirm")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(secretKey.getBytes()))
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    TossErrorResponse errorResponse = objectMapper.readValue(res.getBody(), TossErrorResponse.class);
                    throw new PaymentFailException(errorResponse.message());
                })
                .toEntity(PaymentConfirmResponse.class)
                .getBody();
    }
}
