package roomescape.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "멤버 정보 응답")
public record MemberResponse(

        @Schema(description = "멤버 ID", example = "1")
        long id) {
}
