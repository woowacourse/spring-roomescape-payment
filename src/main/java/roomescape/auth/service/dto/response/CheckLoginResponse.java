package roomescape.auth.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CheckLoginResponse(로그인 체크 응답 DTO)")
public record CheckLoginResponse(String name) {
}
