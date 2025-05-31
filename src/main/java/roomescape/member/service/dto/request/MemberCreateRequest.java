package roomescape.member.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;

public record MemberCreateRequest(
        @NotBlank
        String email,
        @NotBlank
        String password,
        @NotBlank
        String name
) {
    public Member toEntity() {
        return new Member(name, email, password, Role.MEMBER);
    }
}
