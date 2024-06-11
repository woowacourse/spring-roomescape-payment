package roomescape.domain.member.dto;

import roomescape.domain.member.model.Member;
import roomescape.domain.member.model.MemberRole;

public record SaveMemberRequest(
        String email,
        String password,
        String name,
        MemberRole role
) {
    public Member toModel(final String encodedPassword) {
        return new Member(
                MemberRole.USER,
                encodedPassword,
                name,
                email
        );
    }
}
