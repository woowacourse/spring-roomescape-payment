package roomescape.reservation.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.member.docs.ReservationMemberResponseDocs;
import roomescape.reservationTime.docs.ReservationTimeResponseDocs;
import roomescape.theme.docs.ThemeResponseDocs;
import java.time.LocalDate;

@Schema(description = "예약 응답")
public record ReservationResponseDocs(
        @Schema(description = "예약 ID", example = "1")
        Long id,
        @Schema(description = "회원 정보")
        ReservationMemberResponseDocs member,
        @Schema(description = "예약 날짜", example = "2024-03-20")
        LocalDate date,
        @Schema(description = "예약 시간")
        ReservationTimeResponseDocs time,
        @Schema(description = "테마 정보")
        ThemeResponseDocs theme) {

    public static ReservationResponseDocs from(ReservationResponse response) {
        return new ReservationResponseDocs(
                response.id(),
                ReservationMemberResponseDocs.from(response.member()),
                response.date(),
                ReservationTimeResponseDocs.from(response.time()),
                ThemeResponseDocs.from(response.theme())
        );
    }
} 