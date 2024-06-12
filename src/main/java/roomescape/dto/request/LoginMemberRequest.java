package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Member;
import roomescape.domain.Name;
import roomescape.domain.Role;

public record LoginMemberRequest(
        @Schema(description = "회원 엔티티 식별자") long id,
        @Schema(description = "회원 이름") Name name,
        @Schema(description = "회원 역할") Role role
) {
    public static LoginMemberRequest from(Member member) {
        return new LoginMemberRequest(member.getId(), member.getName(), member.getRole());
    }
}
