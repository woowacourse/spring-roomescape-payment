package roomescape.reservation.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservationslot.domain.ReservationSlot;
import roomescape.reservation.domain.Reservation;

public record WaitingWebResponse(
        @Schema(description = "예약 대기 중인 예약의 id") Long reservationId,
        @Schema(description = "예약 대기 중인 멤버의 이름") String name,
        @Schema(description = "예약 대기 중인 테마의 이름") String themeName,
        @Schema(description = "예약 대기 중인 날짜") String date,
        @Schema(description = "예약 대기 중인 시간") String startAt) {

    public static WaitingWebResponse from(final Reservation reservation) {
        ReservationSlot reservationSlot = reservation.getReservationSlot();
        return new WaitingWebResponse(
                reservation.getId(),
                reservation.getMember().getName(),
                reservationSlot.getTheme().getName(),
                reservationSlot.getDate().toString(),
                reservationSlot.getTime().getStartAt().toString()
        );
    }
}
