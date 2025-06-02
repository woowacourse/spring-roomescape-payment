package roomescape.infrastructure.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import roomescape.exception.ExternalApiErrorException;
import roomescape.infrastructure.payment.dto.PaymentApproveErrorResponse;
import roomescape.infrastructure.payment.dto.PaymentApproveRequest;

@Component
public class TossPaymentClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String secretKey;

    public TossPaymentClient(
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper,
            @Value("${payment.secret-key}") String secretKey) {
        this.restClient = restClientBuilder.build();
        this.objectMapper = objectMapper;
        this.secretKey = secretKey;
    }

    public void approvePayment(PaymentApproveRequest paymentApproveRequest) {
        String encodedSecretKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
        restClient.post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
                .body(paymentApproveRequest)
                .header("Authorization", "Basic " + encodedSecretKey)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleError)
                .toBodilessEntity();
    }

    private void handleError(HttpRequest request, ClientHttpResponse response) throws IOException {
        InputStream responseBodyStream = response.getBody();
        String rawResponseBody = StreamUtils.copyToString(responseBodyStream, StandardCharsets.UTF_8);
        PaymentApproveErrorResponse errorResponse = objectMapper.readValue(rawResponseBody,
                PaymentApproveErrorResponse.class);
        throw new ExternalApiErrorException(errorResponse.getMessage());
    }
}
