package roomescape.auth.dto;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "로그인 요청 (client)", description = "해당 파라미터 정보로 사용자가 로그인을 요청한다.")
public record LoginRequest(String email, String password) {
}
