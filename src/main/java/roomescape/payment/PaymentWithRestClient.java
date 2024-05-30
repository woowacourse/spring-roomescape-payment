package roomescape.payment;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;

public class PaymentWithRestClient implements PaymentClient {

    private final RestClient restClient;

    public PaymentWithRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public PaymentResponse confirm(PaymentRequest paymentRequest) {
        return restClient.post()
                .uri("/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic dGVzdF9nc2tfZG9jc19PYVB6OEw1S2RtUVhrelJ6M3k0N0JNdzY6")
                .body(paymentRequest)
                .retrieve()
                .body(PaymentResponse.class);
    }
}
