package roomescape.payment;

import org.springframework.http.ResponseEntity;
import roomescape.payment.dto.PaymentRequest;

public interface PaymentClient {

    ResponseEntity<Void> postPayment(PaymentRequest paymentRequest);
}
