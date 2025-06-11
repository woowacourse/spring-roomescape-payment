package roomescape.domain.member.response;

import roomescape.domain.auth.dto.AccessTokenContent;
import roomescape.domain.member.domain.Member;

public record FindAllMemberResponse(
        Long id,
        String roleName,
        String name
) {

    public FindAllMemberResponse(Member member) {
        this(member.getId(), member.getRole().toString(), member.getName());
    }

    public FindAllMemberResponse(AccessTokenContent accessTokenContent) {
        this(accessTokenContent.id(), accessTokenContent.role().toString(), accessTokenContent.name());
    }
}
