package roomescape.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 체크 응답 DTO")
public record LoginResponse(
        @Schema(description = "발급된 토큰", example = "accessTokenExample")
        String accessToken
) {
}
