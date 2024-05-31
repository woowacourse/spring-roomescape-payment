package roomescape.payment;


import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.reservation.dto.request.ReservationRequest;

@Service
public class PaymentService {

    @Value("${payment.widget.confirm.secret-key}")
    private String widgetSecretKey;

    private final RestClient restClient;

    public PaymentService(RestClient restClient) {
        this.restClient = restClient;
    }

    public void confirm(ReservationRequest reservationRequest) {
        String authorizations = getAuthorizations();

        restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(reservationRequest)
                .header("Authorization", authorizations)
                .retrieve();
    }

    private String getAuthorizations() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
