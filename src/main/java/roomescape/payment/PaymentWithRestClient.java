package roomescape.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;

public class PaymentWithRestClient implements PaymentClient {

    private final RestClient restClient;

    @Value("${security.payment.api.secret-key}")
    private String secretKey;

    public PaymentWithRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public PaymentResponse confirm(PaymentRequest paymentRequest) {
        return restClient.post()
                .uri("/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + secretKey)
                .body(paymentRequest)
                .retrieve()
                .body(PaymentResponse.class);
    }
}
