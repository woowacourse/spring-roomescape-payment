package roomescape.reservation.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.PaymentException;
import roomescape.reservation.dto.request.PaymentConfirmRequest;

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

    public void confirmPayment(PaymentConfirmRequest paymentConfirmRequest) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        try {
            restClient.post()
                    .uri(new URI("https://api.tosspayments.com/v1/payments/confirm"))
                    .header("Authorization", authorizations)
                    .body(paymentConfirmRequest)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        if (response.getStatusCode().is4xxClientError()) {
                            throw new PaymentException(response.getStatusCode(), "status 만 해볼게");
                        }
                    });
        } catch (URISyntaxException e) {
            throw new PaymentException(HttpStatusCode.valueOf(401), e.getMessage());
        }
    }
}
