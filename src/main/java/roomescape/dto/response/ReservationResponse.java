package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Reservation;

import java.time.LocalDate;

public record ReservationResponse(
        @Schema(description = "예약 엔티티 식별자") long id,
        @Schema(description = "예약 날짜") LocalDate date,
        @Schema(description = "예약 시간") ReservationTimeResponse time,
        @Schema(description = "예약 테마") ThemeResponse theme,
        @Schema(description = "예약 회원") LoginMemberResponse member
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getDate(),
                ReservationTimeResponse.from(reservation.getReservationTime()),
                ThemeResponse.from(reservation.getTheme()),
                LoginMemberResponse.from(reservation.getMember())
        );
    }
}
