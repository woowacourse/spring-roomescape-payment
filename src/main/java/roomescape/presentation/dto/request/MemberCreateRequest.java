package roomescape.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "회원 가입 요청 DTO")
public record MemberCreateRequest(
        @Schema(description = "회원 이름", example = "홍길동")
        @NotBlank(message = "사용자 이름은 필수입니다.")
        String name,

        @Schema(description = "회원 이메일", example = "user@example.com")
        @NotBlank(message = "사용자 이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @Schema(description = "회원 비밀번호", example = "password123")
        @NotBlank(message = "사용자 비밀번호는 필수입니다.")
        String password
) {
}
