package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.entity.Member;

@Schema(description = "사용자 응답 DTO 입니다.")
public record MemberResponse(
        @Schema(description = "사용자 ID 입니다.")
        long id,
        @Schema(description = "사용자 이름입니다.")
        String name
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName()
        );
    }
}
