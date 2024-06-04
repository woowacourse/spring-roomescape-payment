package roomescape.domain;

import roomescape.exception.ExceptionType;
import roomescape.exception.RoomescapeException;

public enum ReservationStatus {

    RESERVED_COMPLETE,
    RESERVED_UNPAID,
    PENDING;

    private static final String RESERVED_COMPLETE_MESSAGE = "예약";
    private static final String RESERVED_UNPAID_MESSAGE = "결제대기";
    private static final String PENDING_MESSAGE = "번째 예약대기";

    public String makeStatusMessage(Long rank) {
        if (this == RESERVED_COMPLETE) {
            return RESERVED_COMPLETE_MESSAGE;
        }
        if (this == RESERVED_UNPAID) {
            return RESERVED_UNPAID_MESSAGE;
        }
        if (this == PENDING) {
            return rank + PENDING_MESSAGE;
        }
        throw new RoomescapeException(ExceptionType.NOT_FOUND_RESERVATION_STATUS);
    }

    public boolean isCompleted() {
        return this == RESERVED_COMPLETE;
    }
}
