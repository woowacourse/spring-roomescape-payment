package roomescape.support.fixture;

import static roomescape.domain.member.Role.ADMIN;
import static roomescape.domain.member.Role.NORMAL;

import roomescape.domain.member.Member;
import roomescape.domain.member.Role;

public enum MemberFixture {
    MEMBER_JAZZ("재즈", "jazz@jazz.com", "111", NORMAL),
    MEMBER_SUN("썬", "sun@sun.com", "222", NORMAL),
    MEMBER_BRI("브리", "bri@bri.com", "333", NORMAL),
    MEMBER_SOLAR("솔라", "solar@solar.com", "444", ADMIN);

    private final String name;
    private final String email;
    private final String password;
    private final Role role;

    MemberFixture(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Member create() {
        return new Member(name, email, password, role);
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
