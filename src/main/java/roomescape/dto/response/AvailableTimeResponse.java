package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;

import java.time.LocalTime;
import java.util.List;

public record AvailableTimeResponse(
        @Schema(description = "엔티티 식별자") long id,
        @Schema(description = "예약 시간") LocalTime startAt,
        @Schema(description = "이미 예약된 시간 여부") boolean isBooked
) {
    public static AvailableTimeResponse of(ReservationTime reservationTime, List<Reservation> reservations) {
        return new AvailableTimeResponse(
                reservationTime.getId(),
                reservationTime.getStartAt(),
                reservations.stream()
                        .anyMatch(reservation -> reservation.isReservationTimeOf(reservationTime.getId()))
        );
    }
}
