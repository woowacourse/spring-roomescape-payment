package roomescape.presentation.api.member;

import roomescape.application.member.query.dto.MemberResult;

public record MemberResponse(
        Long id,
        String name
) {

    public static MemberResponse from(final MemberResult memberResult) {
        return new MemberResponse(memberResult.id(), memberResult.name());
    }
}
