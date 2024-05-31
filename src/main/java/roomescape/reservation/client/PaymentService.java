package roomescape.reservation.client;

import static roomescape.reservation.client.errorcode.PaymentConfirmErrorCode.findByErrorCode;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;
import roomescape.common.exception.PaymentException;
import roomescape.reservation.client.errorcode.PaymentConfirmErrorCode;
import roomescape.reservation.controller.dto.response.PaymentErrorResponse;
import roomescape.reservation.service.dto.request.PaymentConfirmRequest;

@Service
public class PaymentService {

    private final RestClient restClient;
    private final String encodedSecretKey;
    private final ObjectMapper objectMapper;

    public PaymentService(
            @Value("${payment.base-url}") String paymentBaseUrl,
            @Value("${payment.secret-key}") String secretKey,
            ObjectMapper objectMapper
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(paymentBaseUrl)
                .requestFactory(getClientHttpRequestFactory())
                .build();
        this.encodedSecretKey = encodeSecretKey(secretKey);
        this.objectMapper = objectMapper;
    }

    public void confirmPayment(PaymentConfirmRequest paymentRequest) {
        try {
            restClient.post()
                    .uri("/v1/payments/confirm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", encodedSecretKey)
                    .body(paymentRequest)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, createPaymentErrorHandler())
                    .body(PaymentErrorResponse.class);
        } catch (ResourceAccessException exception) {
            throw new RuntimeException();
        }
    }

    private ErrorHandler createPaymentErrorHandler() {
        return (request, response) -> {
            PaymentErrorResponse errorResponse = objectMapper.readValue(response.getBody(), PaymentErrorResponse.class);
            throwByCustomErrorResponse(errorResponse);
            throw new PaymentException(HttpStatusCode.valueOf(400), errorResponse.message());
        };
    }

    private static void throwByCustomErrorResponse(PaymentErrorResponse errorResponse) {
        Optional<PaymentConfirmErrorCode> customErrorCode = findByErrorCode(errorResponse.code());
        if (customErrorCode.isPresent()) {
            throw customErrorCode.get().getException();
        }
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(30))
                .withReadTimeout(Duration.ofSeconds(30));
        return ClientHttpRequestFactories.get(settings);
    }

    private static String encodeSecretKey(String secretKey) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
