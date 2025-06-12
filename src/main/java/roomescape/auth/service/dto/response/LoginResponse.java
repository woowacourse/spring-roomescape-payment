package roomescape.auth.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginResponse(로그인 응답 DTO)")
public record LoginResponse(
        String tokenValue
) {
}
