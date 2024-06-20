package roomescape.fixture;

import java.util.List;

import roomescape.domain.member.Member;
import roomescape.domain.member.Name;
import roomescape.domain.member.Role;

public class MemberFixture {

    public static final List<Member> MEMBERS = List.of(
            new Member(1L, new Name("제우스"), "zeus@woowa.com", "1q2w3e4r", Role.ADMIN),
            new Member(2L, new Name("냥인"), "cutehuman@woowa.com", "password", Role.MEMBER),
            new Member(3L, new Name("산초"), "sancho@woowa.com", "password", Role.MEMBER),
            new Member(4L, new Name("호티"), "hotea@woowa.com", "password", Role.MEMBER)
    );

    public static Member memberFixture(int id) {
        return MEMBERS.get(id - 1);
    }
}
