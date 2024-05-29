package roomescape.global.restclient;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.reservation.controller.dto.PaymentRequest;
import roomescape.reservation.controller.dto.PaymentResponse;

public class PaymentWithRestClient {

    private final RestClient restClient;

    public PaymentWithRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

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
