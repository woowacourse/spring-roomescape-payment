package roomescape.dto.request;

import roomescape.global.aop.Sensitive;

public record MemberRegisterRequest(
        String email,
        @Sensitive String password,
        String name
) {
}
