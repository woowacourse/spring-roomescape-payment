package roomescape.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "회원 목록 조회 응답", description = "모든 회원의 정보 조회 응답시 사용됩니다.")
public record MembersResponse(
        @Schema(description = "모든 회원의 ID 및 이름") List<MemberResponse> members
) {
}
