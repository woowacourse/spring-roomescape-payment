package roomescape.fixture;

import roomescape.domain.member.Member;
import roomescape.domain.member.Role;

public class MemberFixture {
    public static Member createMember(String name) {
        return new Member(name, "email123@woowa.net", "password");
    }

    public static Member user() {
        return new Member("mangcho", CommonFixture.userMangEmail, CommonFixture.password, Role.NORMAL);
    }

    public static Member admin() {
        return new Member("admin", CommonFixture.adminEmail, CommonFixture.password, Role.ADMIN);
    }
}
