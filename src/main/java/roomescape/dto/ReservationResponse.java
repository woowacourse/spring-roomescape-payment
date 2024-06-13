package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationStatus;

public record ReservationResponse(
        @Schema(description = "예약 ID")
        long id,

        @Schema(description = "예약자 이름")
        String name,

        @Schema(description = "예약 날짜")
        LocalDate date,

        @Schema(description = "예약 시간")
        ReservationTimeResponse time,

        @Schema(description = "테마 정보")
        ThemeResponse theme,

        @Schema(description = "예약 상태")
        ReservationStatus status
) {
    public static ReservationResponse from(Reservation reservation) {
        ReservationTimeResponse reservationTimeResponse = ReservationTimeResponse.from(reservation.getReservationTime());
        ThemeResponse themeResponse = ThemeResponse.from(reservation.getTheme());

        return new ReservationResponse(
                reservation.getId(),
                reservation.getName(),
                reservation.getDate(),
                reservationTimeResponse,
                themeResponse,
                reservation.getStatus()
        );
    }
}
