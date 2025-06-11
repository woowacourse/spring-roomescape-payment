package roomescape.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import roomescape.member.entity.Member;
import roomescape.member.entity.RoleType;

public record MemberCreateRequest(

        @Schema(description = "이름", example = "신규")
        @NotNull String name,

        @Schema(description = "이메일", example = "new@email.com")
        @NotNull String email,

        @Schema(description = "비밀번호", example = "1234")
        @NotNull String password
) {
    public Member toEntity() {
        return new Member(name, email, password, RoleType.USER);
    }
}
