package roomescape.reservation.client;

import org.springframework.http.ResponseEntity;

import roomescape.reservation.service.PaymentApprovalRequest;

public interface PaymentClient {

    ResponseEntity<Void> approvePayment(PaymentApprovalRequest request);
}
