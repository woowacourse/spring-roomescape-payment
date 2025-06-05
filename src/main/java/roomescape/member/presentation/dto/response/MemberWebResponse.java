package roomescape.member.presentation.dto.response;

import java.util.Optional;
import roomescape.member.domain.Member;

public record MemberWebResponse(Long id, String name) {

    public static MemberWebResponse from(final Optional<Member> optionalMember) {
        if (optionalMember.isEmpty()) {
            return new MemberWebResponse(null, null);
        }
        Member member = optionalMember.get();
        return new MemberWebResponse(member.getId(), member.getName());
    }
}
