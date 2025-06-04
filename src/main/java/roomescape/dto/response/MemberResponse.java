package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.member.Member;

public record MemberResponse(
        @Schema(example = "1")
        long id,

        @Schema(example = "돔푸")
        String name
) {
    public static MemberResponse from(final Member member) {
        return new MemberResponse(member.getId(), member.getName());
    }
}
