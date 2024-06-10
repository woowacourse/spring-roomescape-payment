package roomescape.reservation.controller;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.ReservationConfirmedEvent;

@Component
public class ReservationEventListener {
    private final PaymentService paymentService;

    public ReservationEventListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleReservationConfirmed(ReservationConfirmedEvent event) {
        paymentService.saveReservationPayment(event.reservation(), event.paymentInfo());
    }
}
