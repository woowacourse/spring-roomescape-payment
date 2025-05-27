package roomescape.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.dto.request.ConfirmPaymentRequest;
import roomescape.dto.response.ConfirmPaymentResponse;
import roomescape.exception.custom.PaymentException;
import roomescape.exception.dto.ErrorResponse;

@Service
public class PaymentService {

    private final RestClient restClient;

    public PaymentService(RestClient restClient) {
        this.restClient = restClient;
    }

    public ConfirmPaymentResponse confirmPayment(ConfirmPaymentRequest paymentRequest) {
        String authorizations = getAuthorizationToken();

        return restClient.post()
                .uri("/v1/payment/confirm")
                .header("Authorization", authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    ErrorResponse errorResponse = objectMapper.readValue(response.getBody().readAllBytes(),
                            ErrorResponse.class);
                    throw new PaymentException(response.getStatusCode(), errorResponse.message());
                }))
                .toEntity(ConfirmPaymentResponse.class)
                .getBody();
    }

    private String getAuthorizationToken() {
        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
