package roomescape.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.member.entity.Member;

@Schema(description = "회원 응답 DTO")
public record MemberResponse(
        @Schema(description = "회원 ID", example = "1")
        Long id,

        @Schema(description = "회원 이름", example = "홍길동")
        String name
) {

    public static MemberResponse from(final Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName()
        );
    }
}
