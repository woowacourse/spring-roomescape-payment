package roomescape.reservation.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.auth.dto.LoginMemberResponse;
import roomescape.reservation.entity.Reservation;
import roomescape.theme.dto.ThemeResponse;
import roomescape.time.dto.ReservationTimeResponse;

@Schema(description = "예약 응답")
public record ReservationResponse(
        @Schema(description = "예약 id", defaultValue = "1") long id,
        @Schema(description = "예약 날짜", defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme,
        LoginMemberResponse member) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getDate(),
                ReservationTimeResponse.from(reservation.getReservationTime()),
                ThemeResponse.from(reservation.getTheme()),
                LoginMemberResponse.from(reservation.getLoginMember())
        );
    }
}
