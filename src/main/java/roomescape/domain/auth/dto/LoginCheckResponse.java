package roomescape.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 체크 응답 DTO")
public record LoginCheckResponse(
        @Schema(description = "회원 이름", example = "우가")
        String name
) {
}
