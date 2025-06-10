package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 성공 시 응답 객체")
public record LoginResponse(
        @Schema(description = "로그인한 사용자 이름", example = "김철수")
        String name
) {
}
