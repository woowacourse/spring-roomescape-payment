package roomescape.system.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "로그인 체크 응답", description = "로그인 상태 체크 응답시 사용됩니다.")
public record LoginCheckResponse(
        @Schema(description = "로그인된 회원의 이름") String name
) {
}
