package roomescape.global.restclient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.reservation.controller.dto.PaymentRequest;
import roomescape.reservation.controller.dto.PaymentResponse;
import roomescape.reservation.domain.Payment;

public class PaymentWithRestClient {

    @Value("${security.toss-pay.secret_key}")
    private String secretKey;

    private final RestClient restClient;

    public PaymentWithRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public Payment confirm(PaymentRequest paymentRequest) {
        return restClient.post()
                .uri("/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + createBasicHeader())
                .body(paymentRequest)
                .retrieve()
                .body(Payment.class);
    }

    private String createBasicHeader() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        String tmp = new String(encodedBytes);
        System.out.println(tmp);
        return tmp;
    }

}
