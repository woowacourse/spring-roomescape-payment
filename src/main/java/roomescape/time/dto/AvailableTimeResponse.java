package roomescape.time.dto;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservation.domain.Reservations;
import roomescape.time.entity.ReservationTime;

@Schema(description = "예약 가능 시간 응답")
public record AvailableTimeResponse(
        @Schema(description = "예약 시간 ID", defaultValue = "1") long id,
        @Schema(description = "예약 시간", defaultValue = "23:00") LocalTime startAt,
        @Schema(description = "예약 상태", defaultValue = "true") boolean isBooked) {
    public static AvailableTimeResponse of(ReservationTime reservationTime, Reservations reservations) {
        return new AvailableTimeResponse(
                reservationTime.getId(),
                reservationTime.getStartAt(),
                reservations.hasReservationTimeOf(reservationTime.getId())
        );
    }
}
