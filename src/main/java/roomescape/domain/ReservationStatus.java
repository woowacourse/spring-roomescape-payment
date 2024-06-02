package roomescape.domain;

import java.util.Arrays;
import roomescape.exception.ExceptionType;
import roomescape.exception.RoomescapeException;

public enum ReservationStatus {
    RESERVED_COMPLETE("예약"),
    RESERVED_UNPAID("결제대기"),
    PENDING("예약대기");

    private static final String PENDING_MESSAGE = "번째 예약대기";

    private final String message;

    ReservationStatus(String message) {
        this.message = message;
    }

    public String makeStatusMessage(Long rank) {
        if (this == PENDING) {
            return rank + PENDING_MESSAGE;
        }
        return Arrays.stream(values())
                .filter(this::equals)
                .map(ReservationStatus::getMessage)
                .findAny()
                .orElseThrow(() -> new RoomescapeException(ExceptionType.NOT_FOUND_RESERVATION_STATUS));
    }

    public String getMessage() {
        return message;
    }
}
