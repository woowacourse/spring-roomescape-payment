package roomescape.domain.member.dto;

import roomescape.domain.member.entity.Member;

public record MemberResponse(
        Long id,
        String name
) {

    public static MemberResponse from(final Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName()
        );
    }
}
