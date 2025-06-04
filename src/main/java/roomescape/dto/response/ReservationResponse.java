package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.reservation.Reservation;

import java.time.LocalDate;

public record ReservationResponse(
        @Schema(example = "1")
        long id,

        @Schema(example = "돔푸")
        String name,

        @Schema(example = "2025-06-05")
        LocalDate date,

        ReservationTimeResponse time,

        ReservationThemeResponse theme,

        @Schema(examples = {"예약 확정", "결제 대기", "예약 대기", "예약 거절"})
        String status
) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getMember().getName(),
                reservation.getReservationItem().getDate(),
                ReservationTimeResponse.from(reservation.getReservationItem().getTime()),
                ReservationThemeResponse.from(reservation.getReservationItem().getTheme()),
                reservation.getReservationStatus().description
        );
    }
}
