package roomescape.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.model.Member;
import roomescape.member.model.MemberRole;

public record SaveMemberRequest(
        @Schema(example = "user@mail.com")
        String email,
        String password,
        String name,
        MemberRole role
) {
    public Member toMember(final String encodedPassword) {
        return Member.createMemberWithoutId(
                MemberRole.USER, encodedPassword, name, email
        );
    }
}
