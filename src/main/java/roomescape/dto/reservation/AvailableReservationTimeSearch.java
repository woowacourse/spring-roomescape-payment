package roomescape.dto.reservation;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public record AvailableReservationTimeSearch(
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        Long themeId
) {
}
