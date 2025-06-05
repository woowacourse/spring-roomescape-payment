package roomescape.domain.reservation.dto;

import java.time.LocalTime;

public record AvailableReservationTime(
        Long id,
        LocalTime startAt,
        boolean alreadyBooked
) {
}
