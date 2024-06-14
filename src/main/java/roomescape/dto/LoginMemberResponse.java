package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.LoginMember;

@Schema(description = "로그인 사용자 응답 DTO 입니다.")
public record LoginMemberResponse(
        @Schema(description = "로그인 사용자 ID 입니다.")
        long id,
        @Schema(description = "로그인 사용자 이름입니다.")
        String name
) {
    public static LoginMemberResponse from(LoginMember loginMember) {
        return new LoginMemberResponse(
                loginMember.getId(),
                loginMember.getName()
        );
    }
}
