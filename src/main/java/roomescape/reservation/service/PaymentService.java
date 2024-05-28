package roomescape.reservation.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodySpec;
import roomescape.common.exception.PaymentException;
import roomescape.reservation.dto.request.PaymentConfirmRequest;
import roomescape.reservation.dto.response.PaymentResponse;

@Service
public class PaymentService {

    private final RestClient restClient;
    private final String secretKey;

    public PaymentService(
            RestClient restClient,
            @Value("${payment.secret-key}") String secretKey
    ) {
        this.restClient = restClient;
        this.secretKey = secretKey;
    }

    public PaymentResponse confirmPayment(PaymentConfirmRequest paymentConfirmRequest) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        RequestBodySpec body = restClient.post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
                .header("Authorization", authorizations)
                .body(paymentConfirmRequest);

        try {
            return body.retrieve().body(PaymentResponse.class);

        } catch (HttpClientErrorException e) {
            body.exchange((request, response) -> {
                if (response.getStatusCode().isError()) {
                    throw new PaymentException(response.getStatusCode(), e.getMessage());
                }
                return null;
            });
        }
        return null;
    }
}
