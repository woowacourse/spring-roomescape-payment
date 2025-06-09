package roomescape.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 멤버")
public record LoginMember(
        @Schema(description = "회원 ID", example = "1")
        Long id) {
}
