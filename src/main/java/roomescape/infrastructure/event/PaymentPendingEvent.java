package roomescape.infrastructure.event;

import lombok.Getter;

@Getter
public class PaymentPendingEvent {
    private final Long reservationId;

    public PaymentPendingEvent(Long reservationId) {
        this.reservationId = reservationId;
    }
}
