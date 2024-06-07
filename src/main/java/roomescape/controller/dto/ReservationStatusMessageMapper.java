package roomescape.controller.dto;

import java.util.OptionalLong;

public enum ReservationStatusMessageMapper {
    RESERVED_MESSAGE("예약"),
    WAITING_MESSAGE("번째 예약 대기"),
    ;

    private final String message;

    ReservationStatusMessageMapper(String message) {
        this.message = message;
    }

    public static String mapTo(OptionalLong rank) {
        if(rank.isPresent()) {
            return rank.getAsLong() + WAITING_MESSAGE.message;
        }
        return RESERVED_MESSAGE.message;
    }

    public String getMessage() {
        return message;
    }
}
