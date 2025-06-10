package roomescape.reservationslot.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservation.domain.Reservation;

public record ReservationResponse(
        @Schema(description = "예약 대기할 예약슬롯의 id") Long reservationSlotId,
        @Schema(description = "대기하고 있는 예약의 id") Long waitingId
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(reservation.getReservationSlot().getId(), reservation.getId());
    }
}
