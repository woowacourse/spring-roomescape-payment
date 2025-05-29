package roomescape.domain;

import java.util.Arrays;

public enum Role {

    ADMIN,
    USER
    ;

    public static boolean hasRole(Role role) {
        return Arrays.stream(Role.values())
                .anyMatch(r -> r == role);
    }
}
