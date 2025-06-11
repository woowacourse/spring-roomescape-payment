package roomescape.member.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SignupWebRequest(
        @Schema(description = "멤버 정보에 저장될 이메일") String email,
        @Schema(description = "멤버 정보에 저장될 패스워드") String password,
        @Schema(description = "멤버 정보에 저장될 이름") String name) {
}
