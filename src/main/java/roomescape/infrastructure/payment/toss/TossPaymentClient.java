package roomescape.infrastructure.payment.toss;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static roomescape.exception.code.RestClientErrorCode.RESPONSE_PARSING_ERROR;
import static roomescape.infrastructure.payment.toss.TossPaymentErrorCode.UNKNOWN;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import roomescape.application.request.PaymentInfo;
import roomescape.application.response.PaymentClientResponse;
import roomescape.application.response.TossErrorResponse;
import roomescape.exception.ExternalApiException;
import roomescape.infrastructure.payment.PaymentClient;

@Component
public class TossPaymentClient implements PaymentClient {

    private static final String TOSS_PAYMENT_CONFIRM_URI = "/v1/payments/confirm";
    private static final String SECRET_KEY_PREFIX = "Basic ";
    private static final String DELIMITER = ":";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public TossPaymentClient(final @Value("${payment.toss.secret-key}") String secretKey,
                             final ObjectMapper objectMapper,
                             final @Qualifier("tossClientBuilder") RestClient.Builder restClientBuilder) {
        this.restClient = initRestClient(restClientBuilder, secretKey);
        this.objectMapper = objectMapper;
    }

    public PaymentClientResponse confirmPayment(final PaymentInfo paymentInfo) {
        try {
            return restClient.post().uri(TOSS_PAYMENT_CONFIRM_URI).contentType(MediaType.APPLICATION_JSON)
                    .body(paymentInfo).retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> processErrorResponse(response))
                    .body(PaymentClientResponse.class);
        } catch (RestClientException e) {
            if (e.getCause() instanceof JsonProcessingException
                    || e.getCause() instanceof HttpMessageNotReadableException) {
                throw new ExternalApiException(RESPONSE_PARSING_ERROR);
            }
            throw new ExternalApiException(UNKNOWN, e.getMessage());
        }
    }

    private void processErrorResponse(final ClientHttpResponse response) throws IOException {
        TossErrorResponse error = objectMapper.readValue(response.getBody(), TossErrorResponse.class);
        TossPaymentErrorCode code = TossPaymentErrorCode.from(error.code());
        throw new TossPaymentException(code);
    }

    private RestClient initRestClient(final RestClient.Builder builder, final String secretKey) {
        return builder.defaultHeader(AUTHORIZATION, createAuthHeader(secretKey)).build();
    }

    private String createAuthHeader(final String key) {
        return SECRET_KEY_PREFIX + Base64.getEncoder().encodeToString((key + DELIMITER).getBytes());
    }
}
