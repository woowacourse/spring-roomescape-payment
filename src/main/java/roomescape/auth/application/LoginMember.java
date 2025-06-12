package roomescape.auth.application;

import lombok.Getter;
import roomescape.member.domain.MemberRole;

@Getter
public class LoginMember {

    private final Long id;
    private final String name;
    private final MemberRole role;

    public LoginMember(Long id, String name, MemberRole role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public boolean isNotAdmin() {
        return !role.isAdmin();
    }
}
