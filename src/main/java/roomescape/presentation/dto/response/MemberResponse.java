package roomescape.presentation.dto.response;

import roomescape.business.model.entity.Member;

public record MemberResponse(
        String id,
        String name,
        String email
) {
    public static MemberResponse from(final Member member) {
        return new MemberResponse(member.getId().value(), member.getName().value(), member.getEmail().value());
    }
}
