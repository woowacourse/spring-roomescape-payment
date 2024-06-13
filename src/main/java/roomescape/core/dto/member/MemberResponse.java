package roomescape.core.dto.member;

import roomescape.core.domain.Member;

public record MemberResponse(Long id, String name) {

    public static MemberResponse from(final Member member) {
        final Long id = member.getId();
        final String name = member.getName();

        return new MemberResponse(id, name);
    }
}
