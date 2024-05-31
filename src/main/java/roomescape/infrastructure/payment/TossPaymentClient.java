package roomescape.infrastructure.payment;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import roomescape.dto.payment.PaymentErrorResponse;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.exception.PaymentException;

@Component
public class TossPaymentClient {

    private static final String BASIC = "Basic";
    private static final String API_DELIMITER = ":";

    private final String secretKey;
    private final String confirmUrl;
    private final RestClient restClient;

    public TossPaymentClient(@Value("${api.toss.secret-key}") String secretKey,
                             @Value("${api.toss.url.confirm}") String confirmUrl,
                             RestClient restClient
    ) {
        this.secretKey = secretKey;
        this.confirmUrl = confirmUrl;
        this.restClient = restClient;
    }

    public PaymentResponse confirm(PaymentRequest paymentRequest) {
        try {
            return restClient.post()
                    .uri(confirmUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", createAuthorizationHeader())
                    .body(paymentRequest)
                    .retrieve()
                    .body(PaymentResponse.class);
        } catch (HttpClientErrorException e) {
            PaymentErrorResponse errorResponse = e.getResponseBodyAs(PaymentErrorResponse.class);
            throw new PaymentException(errorResponse.message(), e.getStatusCode());
        }
    }

    private String createAuthorizationHeader() {
        return BASIC + Base64.getEncoder().encodeToString((secretKey + API_DELIMITER).getBytes());
    }
}
