package roomescape.infrastructure.payment;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.domain.dto.PaymentRequest;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;
import roomescape.infrastructure.payment.dto.PaymentResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@EnableConfigurationProperties(TossPaymentClientProperties.class)
public class TossPaymentClient implements PaymentClient {
    private final RestClient restClient;
    private final TossPaymentClientProperties properties;
    private final PaymentErrorHandler paymentErrorHandler;

    public TossPaymentClient(TossPaymentClientProperties properties, PaymentErrorHandler paymentErrorHandler) {
        this.properties = properties;
        this.paymentErrorHandler = paymentErrorHandler;
        this.restClient = getRestClient();
    }

    public RestClient getRestClient() {
        return RestClient.builder()
                .requestFactory(getRequestFactory())
                .baseUrl(properties.baseUrl())
                .defaultStatusHandler(paymentErrorHandler)
                .build();
    }

    private ClientHttpRequestFactory getRequestFactory() {
        return ClientHttpRequestFactories.get(ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(properties.connectionTimeOut())
                .withReadTimeout(properties.readTimeOut()));
    }

    @Override
    public Payment approve(PaymentRequest request) {
        String authorizations = getEncodedKey();
        PaymentResponse response = Optional.ofNullable(restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizations)
                .body(request)
                .retrieve()
                .body(PaymentResponse.class))
                .orElseGet(PaymentResponse::empty);
        return response.toPayment();
    }

    private String getEncodedKey() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((properties.secretKey() + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
