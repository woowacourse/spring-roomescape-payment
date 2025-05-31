package roomescape.application.reservation.event;

import java.time.LocalDate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import roomescape.application.reservation.command.AutoWaitingPromotionService;

@Component
public class DeleteReservationEventListener {

    private final AutoWaitingPromotionService autoWaitingPromotionService;

    public DeleteReservationEventListener(AutoWaitingPromotionService autoWaitingPromotionService) {
        this.autoWaitingPromotionService = autoWaitingPromotionService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void promoteWaitingAfterReservationCanceled(ReservationCancelEvent reservationCancelEvent) {
        LocalDate date = reservationCancelEvent.reservationDate();
        Long reservationTimeId = reservationCancelEvent.reservationTimeId();
        Long themeId = reservationCancelEvent.themeId();
        autoWaitingPromotionService.promote(date, reservationTimeId, themeId);
    }
}
