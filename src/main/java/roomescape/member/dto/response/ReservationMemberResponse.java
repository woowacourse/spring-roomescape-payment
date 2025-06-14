package roomescape.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "예약 멤버 응답")
public record ReservationMemberResponse(
        @Schema(description = "회원 이름", example = "홍길동")
        String name) {
}
