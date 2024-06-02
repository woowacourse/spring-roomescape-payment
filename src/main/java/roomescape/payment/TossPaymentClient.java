package roomescape.payment;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import roomescape.global.exception.PaymentFailException;
import roomescape.global.util.BodyExtractor;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentConfirmResponse;

public class TossPaymentClient {

    private static final String KEY_DELIMITER = ":";
    private final RestClient restClient;

    @Value("${payment.secret-key}")
    private String widgetSecretKey;

    @Value("${payment.api.confirm}")
    private String confirmApi;

    public TossPaymentClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentConfirmResponse confirmPayments(PaymentConfirmRequest request) {
        String secretKey = widgetSecretKey + KEY_DELIMITER;

        return restClient.post()
                .uri(confirmApi)
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(secretKey.getBytes()))
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new PaymentFailException(BodyExtractor.getMessage(res));
                })
                .toEntity(PaymentConfirmResponse.class)
                .getBody();
    }
}
