package roomescape.domain.auth.response;

import roomescape.domain.auth.dto.AccessTokenContent;
import roomescape.domain.member.domain.Member;

public record CheckLoginResponse(
        Long id,
        String roleName,
        String name
) {

    public CheckLoginResponse(Member member) {
        this(member.getId(), member.getRole().toString(), member.getName());
    }

    public CheckLoginResponse(AccessTokenContent accessTokenContent) {
        this(accessTokenContent.id(), accessTokenContent.role().toString(), accessTokenContent.name());
    }
}
