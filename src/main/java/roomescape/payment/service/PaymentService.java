package roomescape.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.exception.ErrorResponse;
import roomescape.exception.PaymentFailException;
import roomescape.payment.dto.PaymentRequest;

@Service
public class PaymentService {

    private static final String KEY_PREFIX = "Basic ";

    private final String paymentSecretKey;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PaymentService(
            @Value("${payment.secret-key}") String paymentSecretKey,
            RestClient restClient,
            ObjectMapper objectMapper
    ) {
        this.paymentSecretKey = paymentSecretKey;
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public void pay(PaymentRequest paymentRequest) {
        restClient.post()
                .uri("/v1/payments/confirm")
                .header(HttpHeaders.AUTHORIZATION, createAuthorizations())
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> handlePaymentErrorResponse(response))
                .toBodilessEntity();
    }

    private String createAuthorizations() {
        return KEY_PREFIX + new String(Base64.getEncoder().encode((paymentSecretKey + ":").getBytes(StandardCharsets.UTF_8)));
    }

    private void handlePaymentErrorResponse(ClientHttpResponse response) throws IOException {
        ErrorResponse paymentErrorResponse = objectMapper.readValue(response.getBody(), ErrorResponse.class);

        throw new PaymentFailException(paymentErrorResponse.code(), paymentErrorResponse.message());
    }
}
