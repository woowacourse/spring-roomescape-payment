package roomescape.auth.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;

@Schema(name = "LoginMember(로그인 멤버 정보 DTO)")
public record LoginMember(
        @NotNull
        Long id,
        @NotBlank
        String name,
        @Email
        String email,
        @NotNull
        Role role
) {

    public static LoginMember of(Member member) {
        return new LoginMember(member.getId(), member.getName(), member.getEmail(), member.getRole());
    }
}
