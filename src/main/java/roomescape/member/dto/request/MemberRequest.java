package roomescape.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MemberRequest(
        @NotBlank(message = "회원 이메일은 비어 있을 수 없습니다.") String email,
        @NotBlank(message = "회원 비밀번호는 비어 있을 수 없습니다.") String password,
        @NotBlank(message = "회원 이름은 비어 있을 수 없습니다.") String name
) {
}
