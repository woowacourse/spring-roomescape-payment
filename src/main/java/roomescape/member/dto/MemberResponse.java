package roomescape.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.domain.Member;

@Schema(name = "회원 조회 응답", description = "회원 정보 조회 응답시 사용됩니다.")
public record MemberResponse(
        @Schema(description = "회원 번호. 회원을 식별할 때 사용합니다.") Long id,
        @Schema(description = "회원의 이름") String name
) {
    public static MemberResponse fromEntity(Member member) {
        return new MemberResponse(member.getId(), member.getName());
    }
}
