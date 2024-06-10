package roomescape.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 로그인 응답")
public record LoginResponse(
        @Schema(description = "사용자 이름", defaultValue = "admin") String name) {
}
