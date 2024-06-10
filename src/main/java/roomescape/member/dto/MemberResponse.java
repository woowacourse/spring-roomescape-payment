package roomescape.member.dto;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "멤버 응답", description = "멤버의 id를 응답한다.")
public record MemberResponse(long id) {
}
