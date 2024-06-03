package roomescape.service.fixture;

import roomescape.model.Member;
import roomescape.model.Role;

public class MemberBuilder {

    private Long id = 1L;
    private String name = "썬";
    private String email = "sun@email.com";
    private String password = "1234";
    private Role role = Role.MEMBER;

    public static MemberBuilder builder() {
        return new MemberBuilder();
    }

    public MemberBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public MemberBuilder name(String name) {
        this.name = name;
        return this;
    }

    public MemberBuilder email(String email) {
        this.email = email;
        return this;
    }

    public MemberBuilder password(String password) {
        this.password = password;
        return this;
    }

    public MemberBuilder role(Role role) {
        this.role = role;
        return this;
    }

    public Member build() {
        return new Member(id, name, role, email, password);
    }
}
