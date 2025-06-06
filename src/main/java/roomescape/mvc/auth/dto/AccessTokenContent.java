package roomescape.mvc.auth.dto;

import roomescape.mvc.member.domain.Role;

public record AccessTokenContent(Long id, Role role, String name) {

    public boolean isAdminToken() {
        return role == Role.ADMIN;
    }
}
