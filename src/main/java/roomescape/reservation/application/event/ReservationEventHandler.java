package roomescape.reservation.application.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.domain.Payment;
import roomescape.reservation.application.ReservationDataService;
import roomescape.reservation.domain.Reservation;

@Component
public class ReservationEventHandler {

    private final ReservationDataService reservationDataService;

    public ReservationEventHandler(final ReservationDataService reservationDataService) {
        this.reservationDataService = reservationDataService;
    }

    @EventListener
    @Transactional
    public void handlePaymentApproved(final PaymentApprovedEvent event) {
        Payment payment = event.payment();
        payment.approve();

        Reservation reservation = reservationDataService.getById(event.reservationId());
        reservation.confirm(payment);
    }

    @EventListener
    @Transactional
    public void handlePaymentFailed(final PaymentFailedEvent event) {
        Payment payment = event.payment();
        payment.fail();

        Reservation reservation = reservationDataService.getById(event.reservationId());
        reservation.paymentFailed(payment);
    }

    @EventListener
    @Transactional
    public void promoteReservation(final ReservationPromoteEvent event) {
        Reservation reservation = reservationDataService.getById(event.reservationId());
        reservation.waitForPayment();
    }
}
