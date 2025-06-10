package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.member.Member;

@Schema(description = "회원 정보 응답 객체")
public record MemberResponse(
        @Schema(description = "회원 ID", example = "1")
        Long id,

        @Schema(description = "회원 이름", example = "김철수")
        String name
) {
    public static MemberResponse from(final Member member) {
        return new MemberResponse(member.getId(), member.getName());
    }
}
