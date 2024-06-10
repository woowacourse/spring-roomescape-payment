package roomescape.reservation.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.entity.Member;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.entity.Reservation;
import roomescape.theme.entity.Theme;
import roomescape.time.entity.ReservationTime;

@Schema(description = "예약 요청")
public record ReservationRequest(
        @Schema(description = "예약 날짜", defaultValue = "#{T(java.time.LocalDate).now()}")
        LocalDate date,
        @Schema(description = "예약 시간 ID", defaultValue = "1")
        long timeId,
        @Schema(description = "테마 ID", defaultValue = "1")
        long themeId) {
    public Reservation toReservation(Member member, ReservationTime reservationTime, Theme theme, ReservationStatus reservationStatus) {
        return new Reservation(this.date, reservationTime, theme, member, reservationStatus);
    }
}
