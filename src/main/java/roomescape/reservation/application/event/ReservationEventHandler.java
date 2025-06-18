package roomescape.reservation.application.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import roomescape.payment.domain.Payment;
import roomescape.reservation.application.ReservationDataService;
import roomescape.reservation.domain.Reservation;

@Component
@Slf4j
public class ReservationEventHandler {

    private final ReservationDataService reservationDataService;

    public ReservationEventHandler(final ReservationDataService reservationDataService) {
        this.reservationDataService = reservationDataService;
    }

    @EventListener
    @Transactional
    public void handlePaymentApproved(final PaymentApprovedEvent event) {
        log.info("결제 승인 이벤트 처리: reservationId={}, paymentId={}",
                event.reservationId(), event.payment().getId());

        Payment payment = event.payment();
        payment.approve();

        Reservation reservation = reservationDataService.getById(event.reservationId());
        reservation.confirm(payment);

        log.info("예약 확정 완료: reservationId={}, paymentId={}",
                reservation.getId(), payment.getId());
    }

    @EventListener
    @Transactional
    public void handlePaymentFailed(final PaymentFailedEvent event) {
        log.warn("결제 실패 이벤트 처리: reservationId={}, paymentId={}",
                event.reservationId(), event.payment().getId());

        Payment payment = event.payment();
        payment.fail();

        Reservation reservation = reservationDataService.getById(event.reservationId());
        reservation.paymentFailed(payment);

        log.warn("예약 결제 실패 처리 완료: reservationId={}", reservation.getId());
    }

    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void promoteReservation(final ReservationPromoteEvent event) {
        log.info("예약 승격 이벤트 처리: reservationId={}", event.reservationId());

        Reservation reservation = reservationDataService.getById(event.reservationId());
        reservation.validateTransitionToPaymentPending();

        log.info("예약 승격 완료: reservationId={}, status=WAITING_FOR_PAYMENT",
                reservation.getId());
    }
}
