package roomescape.infra.payment;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.application.dto.request.PaymentRequest;
import roomescape.exception.PaymentException;

@Component
public class TossPaymentClient implements PaymentClient {

    private final String encodedSecretKey;
    private final String confirmPaymentUrl;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public TossPaymentClient(
            @Value("${payment.secret-key}") String secretKey,
            @Value("${payment.request-url.v1.confirm-payment}") String confirmPaymentUrl,
            RestClient restClient,
            ObjectMapper objectMapper
    ) {
        this.encodedSecretKey = encodeSecretKey(secretKey);
        this.restClient = restClient;
        this.confirmPaymentUrl = confirmPaymentUrl;
        this.objectMapper = objectMapper;
    }

    @Override
    public PaymentResponse confirmPayment(PaymentRequest paymentRequest) {
        return restClient.post()
                .uri(confirmPaymentUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, encodedSecretKey)
                .body(paymentRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    PaymentErrorResponse errorResponse = objectMapper
                            .readValue(res.getBody(), PaymentErrorResponse.class);
                    throw PaymentException.tossPaymentExceptionOf(errorResponse.message());
                })
                .body(PaymentResponse.class);
    }

    private static String encodeSecretKey(String secretKey) {
        return "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }
}
