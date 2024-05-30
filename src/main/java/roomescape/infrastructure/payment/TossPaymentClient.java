package roomescape.infrastructure.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.service.PaymentClient;
import roomescape.service.dto.request.PaymentRequest;

import java.util.Base64;

@Component
public class TossPaymentClient implements PaymentClient {

    private static final String ENCODING_FORMAT = "%s:";
    private static final String SECRET_KEY_PREFIX = "Basic ";

    private final RestClient restClient;
    private final TossPaymentClientErrorHandler tossPaymentClientErrorHandler;
    private final String uri;
    private final String encodedSecretKey;

    public TossPaymentClient(RestClient restClient,
                             TossPaymentClientErrorHandler tossPaymentClientErrorHandler,
                             @Value("${toss.payment-uri}") String uri,
                             @Value("${toss.confirm-path}") String confirmPath,
                             @Value("${toss.secret-key}") String secretKey) {
        this.restClient = restClient;
        this.tossPaymentClientErrorHandler = tossPaymentClientErrorHandler;
        this.uri = uri + confirmPath;
        this.encodedSecretKey = Base64.getEncoder().encodeToString(String.format(ENCODING_FORMAT, secretKey).getBytes());
    }

    @Override
    public void confirm(PaymentRequest paymentRequest) {
        restClient.post()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, SECRET_KEY_PREFIX + encodedSecretKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .onStatus(tossPaymentClientErrorHandler)
                .toBodilessEntity();
    }
}
