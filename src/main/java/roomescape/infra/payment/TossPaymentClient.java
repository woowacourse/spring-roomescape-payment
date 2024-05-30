package roomescape.infra.payment;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.application.PaymentClient;
import roomescape.application.dto.request.PaymentApiRequest;
import roomescape.application.dto.response.PaymentApiResponse;

@Component
public class TossPaymentClient implements PaymentClient {

    private final String encodedSecretKey;
    private final RestClient restClient;
    private final TossPaymentResponseErrorHandler errorHandler;

    public TossPaymentClient(
            @Value("${payment.base-url}") String paymentBaseUrl,
            @Value("${payment.secret-key}") String secretKey,
            RestClient.Builder restClient,
            TossPaymentResponseErrorHandler errorHandler
    ) {
        this.encodedSecretKey = encodeSecretKey(secretKey);
        this.errorHandler = errorHandler;
        this.restClient = restClient.baseUrl(paymentBaseUrl).build();
    }

    public PaymentApiResponse confirmPayment(PaymentApiRequest paymentApiRequest) {
        return restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, encodedSecretKey)
                .body(paymentApiRequest)
                .retrieve()
                .onStatus(errorHandler)
                .body(PaymentApiResponse.class);
    }

    private String encodeSecretKey(String secretKey) {
        return "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }
}
