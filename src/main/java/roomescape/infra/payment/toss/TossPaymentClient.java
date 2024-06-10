package roomescape.infra.payment.toss;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.application.PaymentClient;
import roomescape.application.dto.request.PaymentConfirmApiRequest;
import roomescape.application.dto.response.PaymentConfirmApiResponse;

@Component
@EnableConfigurationProperties(TossPaymentProperties.class)
public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;
    private final String encodedSecretKey;
    private final TossPaymentConfirmErrorHandler errorHandler;

    public TossPaymentClient(
            TossPaymentProperties properties,
            RestClient restClient,
            TossPaymentConfirmErrorHandler errorHandler
    ) {
        this.restClient = restClient.mutate().baseUrl(properties.baseUrl()).build();
        this.encodedSecretKey = encodeSecretKey(properties.secretKey());
        this.errorHandler = errorHandler;
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
