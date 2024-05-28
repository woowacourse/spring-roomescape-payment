package roomescape.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import roomescape.dto.request.reservation.ReservationRequest;
import roomescape.dto.response.PaymentRequest;

@Service
public class TossPaymentService {
    @Value("payment.toss.secret-key")
    private String widgetSecretKey;

    public String createAuthorization() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }

    public PaymentRequest createPaymentRequest(ReservationRequest reservationRequest) {
        return new PaymentRequest(reservationRequest.orderId(), reservationRequest.amount(),
                reservationRequest.paymentKey());
    }
}
