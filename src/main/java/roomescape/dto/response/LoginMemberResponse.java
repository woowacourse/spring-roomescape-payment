package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Member;

public record LoginMemberResponse(
        @Schema(description = "회원 엔티티 식별자") long id,
        @Schema(description = "회원 이름") String name
) {
    public static LoginMemberResponse from(Member member) {
        return new LoginMemberResponse(
                member.getId(),
                member.getName().getValue()
        );
    }
}
