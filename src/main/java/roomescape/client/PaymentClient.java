package roomescape.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.exception.ExternalApiException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class PaymentClient {

    private static final String AUTHORIZATION_PREFIX = "Basic ";

    private final RestClient restClient;

    @Value("${client.payment.secret-key}")
    private String SECRET_KEY;

    public PaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentResponse pay(PaymentRequest request) {
        try {
            return restClient.post()
                    .uri("https://api.tosspayments.com/v1/payments/confirm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", AUTHORIZATION_PREFIX + encodeSecretKey())
                    .body(request)
                    .retrieve()
                    .body(PaymentResponse.class);
        } catch (ResourceAccessException e) {
            throw new ExternalApiException("결제 승인 서버에 문제가 있습니다.");
        }
    }

    private String encodeSecretKey() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encode = encoder.encode((SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));
        return new String(encode);
    }
}
