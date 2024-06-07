package roomescape.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 요청 데이터")
public record LoginRequest(

        @Schema(description = "사용자의 이메일 주소", example = "user@example.com")
        String email,

        @Schema(description = "사용자의 비밀번호", example = "example-password")
        String password) {
}
