package roomescape.domain.time.dto;

import java.time.LocalDate;

public record AvailableReservationTimeRequest(
        LocalDate date,
        Long themeId
) {
}
