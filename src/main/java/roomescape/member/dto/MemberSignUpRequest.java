package roomescape.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberName;

public record MemberSignUpRequest(
        @NotNull @NotBlank String name,
        @NotNull @Email(regexp = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-z]+$") String email,
        @NotNull @NotBlank String password
) {

    public Member toMemberByUserRole() {
        return Member.createMemberByUserRole(new MemberName(name), email, password);
    }
}
