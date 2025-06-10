package roomescape.member.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.domain.Member;

public record SignUpWebResponse(
        @Schema(description = "멤버 엔티티의 기본 키") Long id
) {

    public static SignUpWebResponse from(final Member member) {
        return new SignUpWebResponse(member.getId());
    }
}
