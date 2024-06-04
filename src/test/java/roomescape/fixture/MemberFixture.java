package roomescape.fixture;

import roomescape.domain.member.Member;
import roomescape.domain.member.Role;

public class MemberFixture {
    public static Member createGuest(String name) {
        return new Member(name, name + "@email.com", name + "123", Role.GUEST);
    }

    public static Member createGuest() {
        return new Member("guest", "guest@email.com", "guest123", Role.GUEST);
    }

    public static Member createAdmin() {
        return new Member("admin", "admin@email.com", "admin123", Role.ADMIN);
    }
}
