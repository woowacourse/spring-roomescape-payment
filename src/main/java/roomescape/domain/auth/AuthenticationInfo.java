package roomescape.domain.auth;

import roomescape.domain.user.UserRole;

public record AuthenticationInfo(
    long id,
    UserRole role
) {

    public boolean isAdmin() {
        return UserRole.ADMIN == this.role;
    }

    public boolean isNotAdmin() {
        return !isAdmin();
    }
}
