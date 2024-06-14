package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 요청 DTO 입니다.")
public record LoginRequest(
        @Schema(description = "로그인 이메일입니다.")
        String email,
        @Schema(description = "로그인 비밀번호입니다.")
        String password) {
}
