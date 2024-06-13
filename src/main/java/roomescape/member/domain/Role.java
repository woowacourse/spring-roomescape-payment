package roomescape.member.domain;

import roomescape.exception.ErrorType;
import roomescape.exception.RoomescapeException;

public enum Role {
    USER,
    ADMIN,
    ;

    public static Role of(String roleString) {
        for (Role role : Role.values()) {
            if (roleString.equalsIgnoreCase(role.name())) {
                return role;
            }
        }
        throw new RoomescapeException(ErrorType.UNEXPECTED_SERVER_ERROR);
    }
}
