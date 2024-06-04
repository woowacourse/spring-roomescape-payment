package roomescape.payment.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import roomescape.payment.controller.PaymentClient;
import roomescape.reservation.dto.request.PaymentRequest;
import roomescape.reservation.dto.request.ReservationCreateRequest;

@Service
public class PaymentService {

    private final String widgetSecretKey;
    private final String authorizations;
    private final PaymentClient paymentClient;

    public PaymentService(
            @Value("${toss.secret-key}") String widgetSecretKey,
            PaymentClient paymentClient
    ) {
        this.widgetSecretKey = widgetSecretKey;
        this.paymentClient = paymentClient;
        authorizations = getAuthorizations();
    }

    private String getAuthorizations() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }

    public void pay(ReservationCreateRequest reservationCreateRequest) {
        paymentClient.paymentReservation(authorizations, PaymentRequest.toRequest(reservationCreateRequest))
                .getBody();
    }
}
