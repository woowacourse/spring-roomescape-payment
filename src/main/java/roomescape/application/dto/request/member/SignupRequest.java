package roomescape.application.dto.request.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import roomescape.domain.member.Member;

@Schema(name = "회원 가입 정보")
public record SignupRequest(
        @Schema(description = "회원 이름", example = "망쵸")
        @NotBlank(message = "이름은 공백일 수 없습니다.")
        String name,

        @Schema(description = "이메일", example = "mang@woowa.net")
        @Pattern(
                regexp = "^[a-zA-Z0-9+]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
                message = "이메일 또는 비밀번호를 형식에 맞춰 입력해주세요."
        ) String email,

        @Schema(description = "비밀번호", example = "password")
        @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
        String password
) {

    public Member toMember() {
        return new Member(name, email, password);
    }
}
