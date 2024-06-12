package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Member;

public record MemberResponse(
        @Schema(description = "회원 엔티티 식별자") long id,
        @Schema(description = "회원 이름") String name
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName().getValue()
        );
    }
}
