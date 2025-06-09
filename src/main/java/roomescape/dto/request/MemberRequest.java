package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import roomescape.auth.Role;
import roomescape.domain.Member;

@Schema(description = "멤버 요청 객체")
public record MemberRequest(
        @Email
        @Schema(description = "사용자 이메일", example = "curry@domain.com")
        String email,

        @Schema(description = "비밀번호", example = "curry")
        @NotBlank
        String password,

        @Schema(description = "이름", example = "스테판 커리")
        @NotBlank
        String name
) {

    public Member toMember() {
        return new Member(null, name, email, password, Role.MEMBER);
    }
}
