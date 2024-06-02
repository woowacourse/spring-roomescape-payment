package roomescape.domain.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record BookedReservationReadOnly(
        Long id,
        String name,
        LocalDate date,
        LocalTime time,
        String theme
) {
}
