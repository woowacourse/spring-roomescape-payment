package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "멤버 로그인 요청 객체")
public record LoginRequest(
        @NotBlank
        @Schema(description = "사용자 이메일", example = "curry@domain.com")
        String email,

        @NotBlank
        @Schema(description = "비밀번호", example = "curry")
        String password
) {
}
