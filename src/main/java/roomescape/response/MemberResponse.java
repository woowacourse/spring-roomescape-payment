package roomescape.response;

import roomescape.model.Member;
import roomescape.model.Role;

public class MemberResponse {

    private final Long id;
    private final String name;
    private final Role role;
    private final String email;
    private final String password;

    public MemberResponse(final Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.role = member.getRole();
        this.email = member.getEmail();
        this.password = member.getPassword();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
