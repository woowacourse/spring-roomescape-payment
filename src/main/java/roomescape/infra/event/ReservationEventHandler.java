package roomescape.infra.event;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import roomescape.domain.event.CancelEventPublisher;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;


@Component
@RequiredArgsConstructor
public class ReservationEventHandler {
    private final TaskScheduler taskScheduler;
    private final CancelEventPublisher eventPublisher;
    private final ReservationRepository reservationRepository;

    @EventListener
    public void handlePaymentPendingEvent(PaymentPendingEvent event) {
        taskScheduler.schedule(
                () -> checkPaymentStatusAndProcess(event),
                Instant.now().plus(1, ChronoUnit.MINUTES)
        );
    }

    private void checkPaymentStatusAndProcess(PaymentPendingEvent event) {
        reservationRepository.findById(event.getReservationId())
                .filter(Reservation::isPaymentPending)
                .ifPresent(this::handlePaymentFailure);
    }

    private void handlePaymentFailure(Reservation reservation) {
        cancelReservation(reservation);
        pendingNextReservation(reservation);
    }

    private void cancelReservation(Reservation reservation) {
        reservation.cancelByAdmin();
        reservationRepository.save(reservation);
    }

    private void pendingNextReservation(Reservation canceledReservation) {
        Reservation reservation = reservationRepository.findNextWaitingReservation(canceledReservation.getDetail())
                .orElseThrow(IllegalArgumentException::new);
        reservation.toPending();
        reservationRepository.save(reservation);
        eventPublisher.publishPaymentPendingEvent(reservation);
    }
}
