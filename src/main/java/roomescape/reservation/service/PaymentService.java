package roomescape.reservation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;
import roomescape.common.exception.PaymentException;
import roomescape.reservation.controller.dto.response.PaymentErrorResponse;
import roomescape.reservation.service.dto.request.PaymentConfirmRequest;

@Service
public class PaymentService {

    private final RestClient restClient;
    private final String encodedSecretKey;
    private final ObjectMapper objectMapper;

    public PaymentService(
            @Value("${payment.base-url}") String paymentBaseUrl,
            @Value("${payment.secret-key}") String encodedSecretKey,
            ObjectMapper objectMapper
    ) {
        this.restClient = RestClient.builder().baseUrl(paymentBaseUrl).build();
        this.encodedSecretKey = encodeSecretKey(encodedSecretKey);
        this.objectMapper = objectMapper;
    }

    public void confirmPayment(PaymentConfirmRequest paymentRequest) {
        restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", encodedSecretKey)
                .body(paymentRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, createPaymentErrorHandler())
                .body(PaymentErrorResponse.class);
    }

    private ErrorHandler createPaymentErrorHandler() {
        return (request, response) -> {
            PaymentErrorResponse errorResponse = objectMapper.readValue(response.getBody(), PaymentErrorResponse.class);
            throw new PaymentException(response.getStatusCode(), errorResponse.message());
        };
    }

    private static String encodeSecretKey(String secretKey) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
