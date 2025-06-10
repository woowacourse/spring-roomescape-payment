package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import roomescape.domain.reservation.Reservation;

@Schema(description = "예약 정보 응답 객체")
public record ReservationResponse(
        @Schema(description = "예약 ID", example = "1")
        long id,

        @Schema(description = "예약자 이름", example = "김철수")
        String name,

        @Schema(description = "예약 날짜", example = "2025-06-15")
        LocalDate date,

        @Schema(description = "예약 시간 정보")
        ReservationTimeResponse time,

        @Schema(description = "예약 테마 정보")
        ReservationThemeResponse theme,

        @Schema(description = "예약 상태", example = "ACCEPTED")
        String status
) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getMember().getName(),
                reservation.getReservationItem().getDate(),
                ReservationTimeResponse.from(reservation.getReservationItem().getTime()),
                ReservationThemeResponse.from(reservation.getReservationItem().getTheme()),
                reservation.getReservationStatus().description);
    }
}
