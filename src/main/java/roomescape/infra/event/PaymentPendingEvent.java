package roomescape.infra.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentPendingEvent extends ApplicationEvent {
    private final Long reservationId;

    public PaymentPendingEvent(Object source, Long reservationId) {
        super(source);
        this.reservationId = reservationId;
    }
}
