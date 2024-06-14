package roomescape.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.domain.Member;

public record MemberResponse(
        @Schema(description = "사용자 id", example = "1")
        Long id,
        @Schema(description = "사용자 이름", example = "낙낙")
        String name) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getName());
    }
}
