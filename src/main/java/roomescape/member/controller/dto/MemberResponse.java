package roomescape.member.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import roomescape.member.domain.Member;

@Schema(description = "멤버 응답 정보")
public record MemberResponse(

        @Schema(description = "멤버 ID", example = "1")
        Long id,

        @Schema(description = "멤버 이름", example = "홍길동")
        String name

) {
    public static MemberResponse from(final Member member) {
        return new MemberResponse(member.getId(), member.getName().name());
    }

    public static List<MemberResponse> from(final List<Member> members) {
        return members.stream().map(MemberResponse::from).toList();
    }
}
