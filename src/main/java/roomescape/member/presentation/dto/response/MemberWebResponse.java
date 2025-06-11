package roomescape.member.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.domain.Member;

public record MemberWebResponse(
        @Schema(description = "멤버 엔티티의 기본 키") Long id,
        @Schema(description = "멤버의 이름") String name) {

    public static MemberWebResponse from(final Member member) {
        return new MemberWebResponse(member.getId(), member.getName());
    }
}
