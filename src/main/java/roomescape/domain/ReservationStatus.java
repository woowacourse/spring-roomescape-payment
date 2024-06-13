package roomescape.domain;

import java.util.function.Function;

public enum ReservationStatus {
    RESERVED_COMPLETE(rank -> "예약"),
    RESERVED_UNPAID(rank -> "결제대기"),
    PENDING(rank -> rank + "번째 예약대기");

    private final Function<Long, String> messageFunction;

    ReservationStatus(Function<Long, String> messageFunction) {
        this.messageFunction = messageFunction;
    }

    public String makeStatusMessage(Long rank) {
        return messageFunction.apply(rank);
    }
}
