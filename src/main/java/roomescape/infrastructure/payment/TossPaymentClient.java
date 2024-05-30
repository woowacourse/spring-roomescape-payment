package roomescape.infrastructure.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.service.PaymentClient;
import roomescape.service.dto.request.PaymentRequest;

import java.util.Base64;

@Component
public class TossPaymentClient implements PaymentClient {
    private static final String URI = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String ENCODING_FORMAT = "%s:";

    private final RestClient restClient;
    private final TossPaymentClientErrorHandler tossPaymentClientErrorHandler;
    private final String encodedSecretKey;

    public TossPaymentClient(RestClient restClient,
                             TossPaymentClientErrorHandler tossPaymentClientErrorHandler,
                             @Value("${toss.secret-key}") String secretKey) {
        this.restClient = restClient;
        this.tossPaymentClientErrorHandler = tossPaymentClientErrorHandler;
        this.encodedSecretKey = Base64.getEncoder().encodeToString(String.format(ENCODING_FORMAT, secretKey).getBytes());
    }

    @Override
    public void confirm(PaymentRequest paymentRequest) {
        restClient.post()
                .uri(URI)
                .header("Authorization", "Basic " + encodedSecretKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .onStatus(tossPaymentClientErrorHandler)
                .toBodilessEntity();
    }
}
