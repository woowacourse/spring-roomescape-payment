package roomescape.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.domain.LoginMember;

@Schema(description = "로그인 사용자 응답")
public record LoginMemberResponse(
        @Schema(description = "사용자 ID", defaultValue = "1") long id,
        @Schema(description = "사용자 이름", defaultValue = "admin") String name) {
    public static LoginMemberResponse from(LoginMember loginMember) {
        return new LoginMemberResponse(
                loginMember.getId(),
                loginMember.getName()
        );
    }
}
