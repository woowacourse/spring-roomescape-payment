package roomescape.application.dto.response.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import roomescape.application.dto.response.member.MemberResponse;
import roomescape.application.dto.response.theme.ThemeResponse;
import roomescape.application.dto.response.time.ReservationTimeResponse;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;

@Schema(name = "예약 정보")
public record ReservationResponse(
        @Schema(description = "예약 ID", example = "1")
        Long id,

        @Schema(description = "예약 날짜", example = "2024-08-01", type = "string", format = "date")
        LocalDate date,

        ReservationTimeResponse time,

        ThemeResponse theme,

        MemberResponse member,

        @Schema(description = "예약 상태", example = "RESERVED")
        Status status
) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getDetail().getDate(),
                ReservationTimeResponse.from(reservation.getDetail().getTime()),
                ThemeResponse.from(reservation.getDetail().getTheme()),
                MemberResponse.from(reservation.getMember()),
                reservation.getStatus()
        );
    }
}
