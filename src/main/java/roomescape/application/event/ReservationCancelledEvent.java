package roomescape.application.event;

import java.time.LocalDate;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReservationCancelledEvent extends ApplicationEvent {

    private final LocalDate date;
    private final Long timeSlotId;
    private final Long themeId;

    public ReservationCancelledEvent(Object source, LocalDate date, long timeSlotId, long themeId) {
        super(source);
        this.date = date;
        this.timeSlotId = timeSlotId;
        this.themeId = themeId;
    }
}
