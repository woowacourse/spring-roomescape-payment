package roomescape.reservation.service;


import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.system.payment.PaymentClient;

@Service
public class PaymentService {

    @Value("${payment.widget.confirm.secret-key}")
    private String widgetSecretKey;

    private final PaymentClient paymentClient;

    public PaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public void confirm(ReservationRequest reservationRequest) {
        String authorizations = getAuthorizations();

        paymentClient.confirm(authorizations, reservationRequest);
    }

    private String getAuthorizations() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
