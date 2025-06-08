package roomescape.auth.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.domain.Member;

@Schema(description = "로그인 상태 확인 응답")
public record LoginCheckResponse(
        @Schema(description = "회원 이름")
        String name
) {
    public static LoginCheckResponse from(Member member) {
        return new LoginCheckResponse(member.getName().getValue());
    }
}
