package roomescape.payment.client;

import org.springframework.http.ResponseEntity;
import roomescape.reservation.dto.PaymentApprovalRequest;

public interface PaymentClient {

    ResponseEntity<Void> approvePayment(PaymentApprovalRequest request);
}
