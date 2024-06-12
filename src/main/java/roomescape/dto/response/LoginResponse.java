package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.dto.request.LoginMemberRequest;

public record LoginResponse(
        @Schema(description = "로그인 완료한 회원 이름") String name
) {
    public static LoginResponse from(LoginMemberRequest loginMemberRequest) {
        return new LoginResponse(loginMemberRequest.name().getValue());
    }
}
