package roomescape.infrastructure.event;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import roomescape.domain.event.CancelEventPublisher;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.Theme;
import roomescape.infrastructure.repository.ReservationRepository;

@Component
@RequiredArgsConstructor
public class ReservationEventHandler {
    private final TaskScheduler taskScheduler;
    private final CancelEventPublisher eventPublisher;
    private final ReservationRepository reservationRepository;

    @Value("${payment-timeout}")
    private int paymentTimeout;

    @EventListener
    public void handlePaymentPendingEvent(PaymentPendingEvent event) {
        taskScheduler.schedule(
                () -> checkPaymentStatusAndProcess(event),
                Instant.now().plus(paymentTimeout, ChronoUnit.SECONDS)
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
        reservation.cancel();
        reservationRepository.save(reservation);
    }

    private void pendingNextReservation(Reservation canceledReservation) {
        Theme theme = canceledReservation.getTheme();
        LocalDate date = canceledReservation.getDate();
        ReservationTime time = canceledReservation.getTime();

        reservationRepository.findNextWaiting(theme, date, time)
                .ifPresent(reservation -> {
                    reservation.toPending();
                    reservationRepository.save(reservation);
                    eventPublisher.publishPaymentPendingEvent(reservation);
                });
    }
}
