package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Member;

@Schema(description = "로그인 정보 응답 DTO")
public record LoginMemberResponse(@Schema(example = "1") long id,
                                  @Schema(description = "회원 이름", example = "안돌") String name) {
    public static LoginMemberResponse from(Member member) {
        return new LoginMemberResponse(
                member.getId(),
                member.getName().getValue()
        );
    }
}
