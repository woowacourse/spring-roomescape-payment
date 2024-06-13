package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Member;
import roomescape.domain.Role;

public record MemberInfo(
        @Schema(description = "회원 ID")
        long id,

        @Schema(description = "회원 이름")
        String name,

        @Schema(description = "회원 권한")
        Role role
) {
    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public static MemberInfo from(Member member) {
        return new MemberInfo(member.getId(), member.getName(), member.getRole());
    }
}
