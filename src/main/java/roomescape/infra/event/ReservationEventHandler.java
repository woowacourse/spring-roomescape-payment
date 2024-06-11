package roomescape.infra.event;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import roomescape.domain.event.TimeoutEventPublisher;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;

@Component
@RequiredArgsConstructor
public class ReservationEventHandler {
    private final ReservationTaskScheduler taskScheduler;
    private final TimeoutEventPublisher eventPublisher;
    private final ReservationRepository reservationRepository;

    @Value("${payment-timeout}")
    private int paymentTimeout;

    @EventListener
    public void handlePaymentPendingEvent(PaymentTimeoutEvent event) {
        taskScheduler.schedule(
                () -> checkPaymentStatusAndProcess(event),
                Instant.now().plus(paymentTimeout, ChronoUnit.MINUTES)
        );
    }

    private void checkPaymentStatusAndProcess(PaymentTimeoutEvent event) {
        reservationRepository.findById(event.getReservationId())
                .filter(Reservation::isPending)
                .ifPresent(this::handlePaymentFailure);
    }

    private void handlePaymentFailure(Reservation reservation) {
        cancelReservation(reservation);
        pendingNextReservation(reservation);
    }

    private void pendingNextReservation(Reservation canceledReservation) {
        reservationRepository.findNextWaiting(canceledReservation.getDetail())
                .ifPresent(reservation -> {
                    changeStatusToPending(reservation);
                    eventPublisher.publishTimeoutEvent(reservation);
                });
    }

    private void cancelReservation(Reservation reservation) {
        reservation.toCancel();
        reservationRepository.save(reservation);
    }

    private void changeStatusToPending(Reservation reservation) {
        reservation.toPending();
        reservationRepository.save(reservation);
    }
}
