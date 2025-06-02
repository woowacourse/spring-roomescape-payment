package roomescape.reservation.application.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.application.PaymentService;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentType;
import roomescape.reservation.application.ReservationDataService;
import roomescape.reservation.domain.Reservation;

@Component
public class ReservationEventHandler {

    private final ReservationDataService reservationDataService;
    private final PaymentService paymentService;

    public ReservationEventHandler(final ReservationDataService reservationDataService,
                                   final PaymentService paymentService) {
        this.reservationDataService = reservationDataService;
        this.paymentService = paymentService;
    }

    @EventListener
    @Transactional
    public void handlePaymentApproved(final PaymentApprovedEvent event) {
        Reservation reservation = reservationDataService.getById(event.reservationId());
        Payment payment = new Payment(event.paymentKey(), event.orderId(), event.amount(), PaymentType.NORMAL);
        Payment savedPayment = paymentService.save(payment);
        reservation.confirm(savedPayment);
    }

    @EventListener
    @Transactional
    public void handlePaymentFailed(final PaymentFailedEvent event) {
        Reservation reservation = reservationDataService.getById(event.reservationId());
        reservation.paymentFailed();
    }

    @EventListener
    @Transactional
    public void promoteReservation(final ReservationPromoteEvent event) {
        Reservation reservation = reservationDataService.getById(event.reservationId());
        reservation.waitForPayment();
    }
}
