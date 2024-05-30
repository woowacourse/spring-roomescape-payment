package roomescape.payment.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Encoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentErrorResponse;
import roomescape.payment.exception.PaymentException;
import roomescape.payment.exception.PaymentUnauthorizedException;
import roomescape.payment.exception.RestClientTimeOutException;

@Service
public class PaymentService {
    private static final String BASE_URL = "https://api.tosspayments.com/v1/payments";
    private static final String AUTHORIZATION_PREFIX = "Basic ";
    private static final String SECRET_KEY_SUFFIX = ":";

    private final RestClient restClient;
    private final String authorizationKey;

    public PaymentService(RestClient.Builder builder,
                          @Value("${payment.secret-key}") String secretKey) {
        this.restClient = builder
                .baseUrl(BASE_URL)
                .build();
        this.authorizationKey = initializeAuthorizationKey(secretKey);
    }

    private String initializeAuthorizationKey(String secretKey) {
        Encoder encoder = Base64.getEncoder();
        byte[] encodedSecretKey = encoder.encode((secretKey + SECRET_KEY_SUFFIX).getBytes(StandardCharsets.UTF_8));
        return AUTHORIZATION_PREFIX + new String(encodedSecretKey);
    }

    public void confirmPayment(PaymentConfirmRequest confirmRequest) {
        try {
            restClient.post()
                    .uri("/confirm")
                    .header("Authorization", authorizationKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(confirmRequest)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException exception) {
            handleClientException(exception);
        } catch (ResourceAccessException exception) {
            throw new RestClientTimeOutException(exception);
        }
    }

    private void handleClientException(HttpClientErrorException exception) {
        if (exception.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new PaymentUnauthorizedException();
        }
        PaymentErrorResponse errorResponse = exception.getResponseBodyAs(PaymentErrorResponse.class);
        throw new PaymentException(exception.getStatusCode(), errorResponse.message());
    }
}
