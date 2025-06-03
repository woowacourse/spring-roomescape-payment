package roomescape.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.exception.custom.reason.payment.PaymentConfirmException;
import roomescape.payment.dto.PaymentError;
import roomescape.payment.dto.PaymentRequest;

// toss pg사 이용
@Component
public class PaymentManager {

    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    @Autowired
    public PaymentManager(
            final RestClient.Builder restClientBuilder,
            final ObjectMapper objectMapper,
            @Value("${secret.toss-payment-key}") final String authorizationKey
    ) {
        this.objectMapper = objectMapper;

        restClient = restClientBuilder
                .baseUrl("https://api.tosspayments.com")
                .defaultHeader("Authorization", authorizationKey)
                .build();
    }

    public void confirmPayment(final PaymentRequest paymentRequest) {
        try {
            restClient.post()
                    .uri(URI.create("/v1/payments/confirm"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(paymentRequest)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, ((request, response) -> {
                        final String responseBody = new String(response.getBody().readAllBytes());
                        final PaymentError error = objectMapper.readValue(responseBody, PaymentError.class);
                        throw new PaymentConfirmException(error);
                    }))
                    .toBodilessEntity();
        } catch (final ResourceAccessException e) {
            final PaymentError error = PaymentError.connectFail();
            throw new PaymentConfirmException(error);
        }
    }
}
