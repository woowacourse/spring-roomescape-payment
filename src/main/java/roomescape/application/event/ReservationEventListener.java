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
        log.info(
                "예약 삭제 이벤트 수신 - date: {}, timeSlotId: {}, themeId: {}",
                event.getDate(),
                event.getTimeSlotId(),
                event.getThemeId()
        );
        waitingService.approveNextWaiting(event.getDate(), event.getTimeSlotId(), event.getThemeId());
    }
}
