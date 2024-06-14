package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답 DTO 입니다.")
public record LoginResponse(@Schema(description = "로그인 사용자 이름입니다.") String name) {
}
