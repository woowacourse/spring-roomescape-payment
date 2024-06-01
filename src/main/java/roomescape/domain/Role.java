package roomescape.domain;

import roomescape.exception.ExceptionType;
import roomescape.exception.RoomescapeException;

import java.util.Arrays;

public enum Role {
    ADMIN,
    USER;

    public static Role from(String name) {
        return Arrays.stream(values())
                .filter(role -> role.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new RoomescapeException(ExceptionType.NOT_FOUND_ROLE));
    }
}
