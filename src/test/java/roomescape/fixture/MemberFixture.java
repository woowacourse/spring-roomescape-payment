package roomescape.fixture;

import roomescape.domain.member.Member;
import roomescape.domain.member.Role;

public class MemberFixture {

    public static Member createAdmin() {
        return new Member("admin", "admin@email.com", "admin123", Role.ADMIN);
    }

    public static Member createGuest() {
        return new Member("lini", "lini@email.com", "lini123", Role.GUEST);
    }

    public static Member createGuest(String name, String email, String password) {
        return new Member(name, email, password, Role.GUEST);
    }
}
