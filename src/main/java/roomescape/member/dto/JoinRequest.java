package roomescape.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;

public record JoinRequest(
        @Schema(description = "회원가입 이메일", example = "example@mail.com")
        @NotBlank(message = "회원가입 이메일은 필수입니다")
        @Email(message = "이메일 형태가 아닙니다")
        @Size(max = 320, message = "이메일의 길이를 다시 확인해주세요")
        String email,

        @Schema(description = "회원가입 비밀번호", example = "123456")
        @NotBlank(message = "회원가입 비밀번호는 필수입니다")
        @Size(min = 6, max = 20, message = "비밀번호의 길이를 다시 확인해주세요")
        String password,

        @Schema(description = "회원가입 이름", example = "홍길동")
        @NotBlank(message = "이름은 필수 입니다")
        @Size(max = 20, message = "이름의 길이를 다시 확인해주세요")
        String name
) {

    public Member toMember() {
        return new Member(name, email, password, Role.MEMBER);
    }
}
