package roomescape.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberName;

public record MemberSignUpRequest(
        @NotNull
        @NotBlank
        @Schema(description = "회원가입 이름", example = "테스트")
        String name,

        @NotNull
        @Email(regexp = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-z]+$")
        @Schema(description = "회원가입 이메일", example = "test@email.com")
        String email,

        @Schema(description = "회원가입 비밀번호", example = "1234")
        @NotNull @NotBlank String password
) {

    public Member toMemberByUserRole() {
        return Member.createMemberByUserRole(new MemberName(name), email, password);
    }
}
