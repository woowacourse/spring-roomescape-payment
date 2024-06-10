package roomescape.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.entity.Member;

@Schema(description = "사용자 응답")
public record MemberResponse(
        @Schema(description = "사용자 ID", defaultValue = "1") long id,
        @Schema(description = "사용자 이름", defaultValue = "admin") String name) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName()
        );
    }
}
