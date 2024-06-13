package roomescape.core.dto.member;

import roomescape.core.domain.Member;

public record LoginMember(Long id) {
    public static LoginMember from(final Member member) {
        return new LoginMember(member.getId());
    }
}
