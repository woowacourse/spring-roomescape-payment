package roomescape.api;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import roomescape.dto.PaymentErrorResponse;
import roomescape.dto.PaymentRequest;
import roomescape.dto.PaymentResponse;
import roomescape.exception.PaymentException;
import roomescape.service.PaymentClient;

public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;

    @Value("${security.api.toss.secret-key}")
    private String widgetSecretKey;
    String authorizations;

    public TossPaymentClient(RestClient restClient) {
        this.restClient = restClient;
        Base64.Encoder encoder = Base64.getEncoder();
        authorizations = "Basic " + new String(
                encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public void pay(PaymentRequest paymentRequest) {
        try {
            PaymentResponse body = restClient.post()
                    .uri("v1/payments/confirm")
                    .header("Authorization", authorizations)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(paymentRequest)
                    .retrieve()
                    .body(PaymentResponse.class);
        } catch (HttpClientErrorException e) {
            throw new PaymentException(e.getResponseBodyAs(PaymentErrorResponse.class), e.getStatusCode());
        }
    }
}
