package roomescape.application.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import roomescape.application.WaitingService;

@Slf4j
@Component
public class ReservationEventListener {

    private final WaitingService waitingService;

    public ReservationEventListener(WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @EventListener
    public void handleReservationCancelled(ReservationCancelledEvent event) {
        waitingService.approveNextWaiting(event.getDate(), event.getTimeSlotId(), event.getThemeId());
    }
}
