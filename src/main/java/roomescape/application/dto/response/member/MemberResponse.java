package roomescape.application.dto.response.member;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.member.Member;

@Schema(name = "회원 정보")
public record MemberResponse(
        @Schema(description = "회원 ID", example = "1")
        Long id,

        @Schema(description = "회원 이름", example = "망쵸")
        String name
) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getName());
    }
}
