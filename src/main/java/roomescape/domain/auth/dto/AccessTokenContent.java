package roomescape.domain.auth.dto;

import roomescape.domain.member.domain.Role;

public record AccessTokenContent(Long id, Role role, String name) {

    public boolean isAdminToken() {
        return role == Role.ADMIN;
    }
}
