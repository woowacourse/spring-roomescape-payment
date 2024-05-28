package roomescape.reservation.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Encoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
public class PaymentService {
    private static final String BASE_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String AUTHORIZATION_PREFIX = "Basic ";
    private static final String SECRET_KEY_SUFFIX = ":";

    private final RestClient restClient;
    private final String authorizationKey;

    public PaymentService(@Value("${payment.secret-key}") String secretKey) {
        this.restClient = RestClient.builder().baseUrl(BASE_URL).build();
        this.authorizationKey = createAuthorizationKey(secretKey);
    }

    private String createAuthorizationKey(String secretKey) {
        Encoder encoder = Base64.getEncoder();
        byte[] encodedSecretKey = encoder.encode((secretKey + SECRET_KEY_SUFFIX).getBytes(StandardCharsets.UTF_8));
        return AUTHORIZATION_PREFIX + new String(encodedSecretKey);
    }

    public void confirmPayment(PaymentConfirmRequest confirmRequest) {
        try {
            restClient.post()
                    .header("Authorization", authorizationKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(confirmRequest)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException exception) {
            throw new PaymentException(exception.getStatusCode(), exception.getResponseBodyAsString());
        }
    }
}
