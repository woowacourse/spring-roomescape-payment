package roomescape.common.security.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.domain.Member;

public record CheckLoginResponse(@Schema(description = "현재 로그인된 멤버의 이름") String name) {

    public static CheckLoginResponse from(final Member member) {
        return new CheckLoginResponse(member.getName());
    }
}
