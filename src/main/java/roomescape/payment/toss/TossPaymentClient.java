package roomescape.payment.toss;

import static org.springframework.web.client.RestClient.Builder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Encoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.common.config.TossPaymentsProperties;
import roomescape.payment.PaymentClient;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentConfirmResponse;

@Component
public class TossPaymentClient implements PaymentClient {

    private RestClient restClient;

    public TossPaymentClient(TossPaymentsProperties properties, Builder tossRestClientBuilder) {
            restClient = tossRestClientBuilder
                .defaultStatusHandler(new TossPaymentErrorHandler())
                .defaultHeader("Authorization", getAuthorization(properties.getSecretKey()))
                .defaultHeader("Content-Type", "application/json")
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    @Override
    public PaymentConfirmResponse confirm(PaymentConfirmRequest request) {
        return restClient.post()
                .body(request)
                .retrieve()
                .body(PaymentConfirmResponse.class);
    }

    private String getAuthorization(String secretKey) {
        return "Basic " + encodeToBase64((secretKey + ":"));
    }

    private String encodeToBase64(String value) {
        Encoder encoder = Base64.getEncoder();
        return new String(encoder.encode(value.getBytes(StandardCharsets.UTF_8)));
    }
}
