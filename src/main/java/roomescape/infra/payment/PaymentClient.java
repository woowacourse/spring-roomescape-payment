package roomescape.infra.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.application.dto.request.PaymentRequest;
import roomescape.exception.PaymentException;

@Component
public class PaymentClient {

    private final String encodedSecretKey;
    private final RestClient restClient;
    private final String confirmPaymentUrl;
    private final ObjectMapper objectMapper;

    public PaymentClient(
            @Value("${payment.base-url}") String paymentBaseUrl,
            @Value("${payment.secret-key}") String secretKey,
            @Value("${payment.request-url.v1.confirm-payment}") String confirmPaymentUrl,
            ObjectMapper objectMapper
    ) {
        this.encodedSecretKey = encodeSecretKey(secretKey);
        this.restClient = RestClient.builder().baseUrl(paymentBaseUrl).build();
        this.confirmPaymentUrl = confirmPaymentUrl;
        this.objectMapper = objectMapper;
    }

    public void confirmPayment(PaymentRequest paymentRequest) {
        restClient.post()
                .uri(confirmPaymentUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, encodedSecretKey)
                .body(paymentRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    PaymentErrorResponse errorResponse = objectMapper
                            .readValue(res.getBody(), PaymentErrorResponse.class);
                    throw PaymentException.tossPaymentExceptionOf(errorResponse.message());
                });
    }

    private static String encodeSecretKey(String secretKey) {
        return "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }
}
