package roomescape.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 체크 응답 DTO")
public record LoginMember(
        @Schema(description = "회원 이메일", example = "wooga@gmail.com")
        String email
) {
}
