package roomescape.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;
import roomescape.global.util.Encoder;
import roomescape.payment.TossPaymentProperties;
import roomescape.payment.exception.PaymentException;
import roomescape.payment.service.dto.PaymentErrorResponse;
import roomescape.payment.service.dto.PaymentRequest;
import roomescape.payment.service.dto.PaymentResponse;

@Component
public class PaymentClient {

    private final RestClient restClient;

    private final Encoder encoder;

    private final TossPaymentProperties tossPaymentProperties;

    private final ObjectMapper objectMapper;

    public PaymentClient(RestClient restClient,
                         Encoder encoder,
                         TossPaymentProperties tossPaymentProperties,
                         ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.encoder = encoder;
        this.tossPaymentProperties = tossPaymentProperties;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<PaymentResponse> confirm(PaymentRequest paymentRequest) {
        return restClient.post()
                .uri(tossPaymentProperties.getConfirmUrl())
                .header("Authorization", getEncodeKey())
                .body(paymentRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, handleError())
                .toEntity(PaymentResponse.class);
    }

    public ResponseEntity<PaymentResponse> cancel(String paymentKey) {
        Map<String, String> params = new HashMap<>();
        String cancelReason = "단순 변심";
        params.put("cancelReason", cancelReason);

        return restClient.post()
                .uri(String.format(tossPaymentProperties.getCancelUrl(), paymentKey))
                .header("Authorization", getEncodeKey())
                .body(params)
                .retrieve()
                .onStatus(HttpStatusCode::isError, handleError())
                .toEntity(PaymentResponse.class);
    }

    private ErrorHandler handleError() {
        return (request, response) -> {
            throw new PaymentException(objectMapper.readValue(response.getBody(), PaymentErrorResponse.class));
        };
    }

    private String getEncodeKey() {
        return encoder.encode(tossPaymentProperties.getSecretKey());
    }
}
