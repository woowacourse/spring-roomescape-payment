package roomescape;

import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.member.domain.Password;

public class TestFixture {

    public static final Member ADMIN_MEMBER = Member.builder()
            .name("관리자")
            .role(MemberRole.ADMIN)
            .email("admin@email.com")
            .password(Password.createForMember("password"))
            .build();


}
