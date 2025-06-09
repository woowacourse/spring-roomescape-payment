package roomescape.member.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.dto.response.ReservationMemberResponse;

@Schema(description = "예약 멤버 응답")
public record ReservationMemberResponseDocs(
        @Schema(description = "회원 이름", example = "홍길동")
        String name) {

    public static ReservationMemberResponseDocs from(ReservationMemberResponse response) {
        return new ReservationMemberResponseDocs(response.name());
    }
} 