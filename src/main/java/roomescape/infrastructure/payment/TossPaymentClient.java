package roomescape.infrastructure.payment;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.exception.PaymentServerException;
import roomescape.service.PaymentClient;
import roomescape.service.dto.request.PaymentRequest;

import java.util.Base64;

@Component
@EnableConfigurationProperties(TossPaymentProperties.class)
public class TossPaymentClient implements PaymentClient {

    private static final String ENCODING_FORMAT = "%s:";
    private static final String SECRET_KEY_PREFIX = "Basic ";

    private final RestClient restClient;
    private final TossPaymentClientErrorHandler errorHandler;
    private final TossPaymentProperties properties;
    private final String encodedSecretKey;

    public TossPaymentClient(RestClient restClient, TossPaymentClientErrorHandler errorHandler, TossPaymentProperties properties) {
        this.restClient = restClient;
        this.errorHandler = errorHandler;
        this.properties = properties;
        this.encodedSecretKey = getEncodedSecretKey(properties.getSecretKey());
    }

    private String getEncodedSecretKey(String secretKey) {
        return Base64.getEncoder().encodeToString(String.format(ENCODING_FORMAT, secretKey).getBytes());
    }

    @Override
    public void confirm(PaymentRequest paymentRequest) {
        try {
            restClient.post()
                    .uri(properties.getPaymentUri())
                    .header(HttpHeaders.AUTHORIZATION, SECRET_KEY_PREFIX + encodedSecretKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(paymentRequest)
                    .retrieve()
                    .onStatus(errorHandler)
                    .toBodilessEntity();
        } catch (ResourceAccessException e) {
            throw new PaymentServerException(e.getMessage(), e);
        }
    }
}
