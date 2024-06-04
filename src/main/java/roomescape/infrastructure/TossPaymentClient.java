package roomescape.infrastructure;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.PaymentClient;
import roomescape.payment.TossPaymentClientErrorHandler;
import roomescape.service.dto.request.PaymentRequest;

@Component
public class TossPaymentClient implements PaymentClient {

    private static final String ENCODING_FORMAT = "%s:";

    private final RestClient restClient;
    private final TossPaymentClientErrorHandler tossPaymentClientErrorHandler;
    private final String uri;
    private final String encodedSecretKey;

    public TossPaymentClient(RestClient restClient,
                             TossPaymentClientErrorHandler tossPaymentClientErrorHandler,
                             @Value("${toss.url}") String uri,
                             @Value("${toss.secret-key}") String secretKey) {
        this.restClient = restClient;
        this.tossPaymentClientErrorHandler = tossPaymentClientErrorHandler;
        this.uri = uri;
        this.encodedSecretKey = Base64.getEncoder()
                .encodeToString(String.format(ENCODING_FORMAT, secretKey).getBytes());
    }

    @Override
    public void confirm(PaymentRequest paymentRequest) {
        restClient.post()
                .uri(uri)
                .header("Authorization", "Basic " + encodedSecretKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .onStatus(tossPaymentClientErrorHandler)
                .toBodilessEntity();
    }
}
