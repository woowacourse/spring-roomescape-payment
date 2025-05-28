package roomescape.payment.infrastructure;

import java.util.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import roomescape.reservation.dto.request.PaymentRequest;
import roomescape.reservation.dto.response.FailureResponse;
import roomescape.reservation.dto.response.PaymentResponse;

public class TossApiClient {

    private final String key = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:";
    private String kkey = Base64.getEncoder().encodeToString(key.getBytes());

    private final RestClient restClient;

    public TossApiClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentResponse authPayment(PaymentRequest paymentRequest) {
        try {
            return restClient.post()
                    .uri("https://api.tosspayments.com/v1/payments/confirm")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + kkey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(paymentRequest)
                    .retrieve()
                    .toEntity(PaymentResponse.class)
                    .getBody();
        } catch (HttpClientErrorException e) {
            FailureResponse responseBodyAs = e.getResponseBodyAs(FailureResponse.class);
            return null;
        }
    }

}
