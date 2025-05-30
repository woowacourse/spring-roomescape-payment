package roomescape.infrastructure.payment.toss;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.application.request.PaymentInfo;
import roomescape.application.response.PaymentResponse;
import roomescape.application.response.TossErrorResponse;
import roomescape.exception.PaymentException;
import roomescape.infrastructure.payment.PaymentClient;
import roomescape.infrastructure.payment.PaymentErrorCode;

@Component
public class TossPaymentClient implements PaymentClient {

    private static final String TOSS_PAYMENT_CONFIRM_URI = "/v1/payments/confirm";
    private static final String SECRET_KEY_PREFIX = "Basic ";
    private static final String DELIMITER = ":";

    private final String secretKey;
    private final RestClient restClient;

    public TossPaymentClient(final @Value("${payment.toss.secret-key}") String secretKey,
                             final ObjectMapper objectMapper, final RestClient.Builder restClientBuilder) {
        this.secretKey = secretKey;
        this.restClient = initRestClient(restClientBuilder, objectMapper);
    }

    public PaymentResponse confirmPayment(final PaymentInfo paymentInfo) {
        String encodedKey = getEncodedKey(secretKey + DELIMITER);
        return restClient.post().uri(TOSS_PAYMENT_CONFIRM_URI).header(AUTHORIZATION, SECRET_KEY_PREFIX + encodedKey)
                .contentType(MediaType.APPLICATION_JSON).body(paymentInfo).retrieve().body(PaymentResponse.class);
    }

    private RestClient initRestClient(final RestClient.Builder builder, final ObjectMapper objectMapper) {
        return builder.defaultStatusHandler(status -> status.is4xxClientError() || status.is5xxServerError(),
                (request, response) -> {
                    TossErrorResponse error = objectMapper.readValue(response.getBody(), TossErrorResponse.class);
                    PaymentErrorCode code = PaymentErrorCode.from(error.code());
                    throw new PaymentException(code);
                }).build();
    }

    private String getEncodedKey(final String key) {
        return Base64.getEncoder().encodeToString(key.getBytes());
    }
}
