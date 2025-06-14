package roomescape.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 체크 응답")
public record LoginCheckResponse(
        @Schema(description = "회원 이름", example = "홍길동")
        String name) {
}
