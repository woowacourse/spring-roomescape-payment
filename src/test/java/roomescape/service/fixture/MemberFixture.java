package roomescape.service.fixture;

import roomescape.model.Member;
import roomescape.model.Role;


public enum MemberFixture {
    GENERAL(1L, "포케", "poke@monster.com", "1234", Role.MEMBER),
    ADMIN(2L, "썬", "sun@email.com", "1234", Role.ADMIN);

    private Long id;
    private String name;
    private String email;
    private String password;
    private Role role;

    MemberFixture(final Long id, final String name, final String email, final String password, final Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Member getMember() {
        return new Member(id, name, role, email, password);
    }
}
