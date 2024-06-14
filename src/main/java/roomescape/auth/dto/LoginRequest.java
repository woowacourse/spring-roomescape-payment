package roomescape.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @Schema(description = "사용자 이메일", example = "nak@abc.com")
        String email,
        @Schema(description = "사용자 비밀번호", example = "1234")
        String password) {
}
