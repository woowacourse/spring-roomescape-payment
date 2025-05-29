package roomescape.application.reservation.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import roomescape.application.reservation.command.AutoWaitingPromotionService;

import java.time.LocalDate;

@Component
public class DeleteReservationEventListener {

    private final AutoWaitingPromotionService autoWaitingPromotionService;

    public DeleteReservationEventListener(final AutoWaitingPromotionService autoWaitingPromotionService) {
        this.autoWaitingPromotionService = autoWaitingPromotionService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void promoteWaitingAfterReservationCanceled(final ReservationCancelEvent reservationCancelEvent) {
        final LocalDate date = reservationCancelEvent.reservationDate();
        final Long reservationTimeId = reservationCancelEvent.reservationTimeId();
        final Long themeId = reservationCancelEvent.themeId();
        autoWaitingPromotionService.promote(date, reservationTimeId, themeId);
    }
}
