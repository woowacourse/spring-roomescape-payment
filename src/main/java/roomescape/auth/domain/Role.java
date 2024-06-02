package roomescape.auth.domain;

import java.util.Arrays;
import roomescape.auth.exception.UnauthenticatedUserException;

public enum Role {
    USER,
    ADMIN,
    ;

    public static Role from(String roleData) {
        return Arrays.stream(values())
                .filter(role -> role.name().equals(roleData))
                .findAny()
                .orElseThrow(() -> new UnauthenticatedUserException("일치하는 권한이 없습니다."));
    }

    public boolean isUser() {
        return this == USER;
    }
}
