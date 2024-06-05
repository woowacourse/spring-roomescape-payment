package roomescape.infra.payment.toss;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.application.PaymentClient;
import roomescape.application.dto.request.PaymentConfirmApiRequest;
import roomescape.application.dto.response.PaymentConfirmApiResponse;

@Component
public class TossPaymentClient implements PaymentClient {

    private final String encodedSecretKey;
    private final RestClient restClient;
    private final TossPaymentConfirmErrorHandler errorHandler;

    public TossPaymentClient(
            @Value("${payment.base-url}") String paymentBaseUrl,
            @Value("${payment.secret-key}") String secretKey,
            RestClient restClient,
            TossPaymentConfirmErrorHandler errorHandler
    ) {
        this.encodedSecretKey = encodeSecretKey(secretKey);
        this.errorHandler = errorHandler;
        this.restClient = restClient.mutate().baseUrl(paymentBaseUrl).build();
    }

    @Override
    public PaymentConfirmApiResponse confirmPayment(PaymentConfirmApiRequest request) {
        return restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, encodedSecretKey)
                .body(request)
                .retrieve()
                .onStatus(errorHandler)
                .body(PaymentConfirmApiResponse.class);
    }

    private String encodeSecretKey(String secretKey) {
        return "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }
}
