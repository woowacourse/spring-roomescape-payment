package roomescape.reservation.presentation.dto.response;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.presentation.dto.response.MemberWebResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservationslot.domain.ReservationSlot;
import roomescape.reservationtime.presentation.dto.response.ReservationTimeWebResponse;
import roomescape.theme.presentation.dto.response.ThemeWebResponse;

public record ConfirmedReservationWebResponse(
        @Schema(description = "예약 엔티티의 기본 키") Long id,
         @Schema(description = "예약한 멤버") MemberWebResponse member,
         @Schema(description = "예약한 날짜") LocalDate date,
         @Schema(description = "예약한 시간") ReservationTimeWebResponse time,
         @Schema(description = "예약한 테마") ThemeWebResponse theme
) {
    public static ConfirmedReservationWebResponse of(ReservationSlot reservationSlot) {
        Reservation confirmedReservation = reservationSlot.findConfirmedReservation();
        return new ConfirmedReservationWebResponse(confirmedReservation.getId(),
                MemberWebResponse.from(reservationSlot.findConfirmedMember()), reservationSlot.getDate(),
                ReservationTimeWebResponse.from(reservationSlot.getTime()),
                ThemeWebResponse.from(reservationSlot.getTheme())
        );
    }
}
