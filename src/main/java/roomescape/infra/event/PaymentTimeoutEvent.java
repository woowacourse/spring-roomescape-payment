package roomescape.infra.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentTimeoutEvent extends ApplicationEvent {
    private final Long reservationId;

    public PaymentTimeoutEvent(Object source, Long reservationId) {
        super(source);
        this.reservationId = reservationId;
    }
}
