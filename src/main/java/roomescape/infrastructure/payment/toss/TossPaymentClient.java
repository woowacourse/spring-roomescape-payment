package roomescape.infrastructure.payment.toss;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
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

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public TossPaymentClient(final @Value("${payment.toss.secret-key}") String secretKey,
                             final ObjectMapper objectMapper, final RestClient.Builder restClientBuilder) {
        this.restClient = initRestClient(restClientBuilder, secretKey);
        this.objectMapper = objectMapper;
    }

    public PaymentResponse confirmPayment(final PaymentInfo paymentInfo) {
        return restClient.post().uri(TOSS_PAYMENT_CONFIRM_URI).contentType(MediaType.APPLICATION_JSON).body(paymentInfo)
                .retrieve().onStatus(HttpStatusCode::isError, (request, response) -> processErrorResponse(response))
                .body(PaymentResponse.class);
    }

    private void processErrorResponse(final ClientHttpResponse response) throws IOException {
        TossErrorResponse error = objectMapper.readValue(response.getBody(), TossErrorResponse.class);
        PaymentErrorCode code = PaymentErrorCode.from(error.code());
        throw new PaymentException(code);
    }

    private RestClient initRestClient(final RestClient.Builder builder, final String secretKey) {
        return builder.defaultHeader(AUTHORIZATION, createAuthHeader(secretKey)).build();
    }

    private String createAuthHeader(final String key) {
        return SECRET_KEY_PREFIX + Base64.getEncoder().encodeToString((key + DELIMITER).getBytes());
    }
}
