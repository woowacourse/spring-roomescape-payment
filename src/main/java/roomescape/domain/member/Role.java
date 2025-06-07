package roomescape.domain.member;

import roomescape.exception.AccessDeniedException;

public enum Role {
    USER,
    ADMIN,
    ;

    public boolean isAdmin() {
        return this == ADMIN;
    }
}
