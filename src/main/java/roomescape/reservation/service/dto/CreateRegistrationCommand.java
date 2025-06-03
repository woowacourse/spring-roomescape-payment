package roomescape.reservation.service.dto;

import java.time.LocalDate;

public record CreateRegistrationCommand(
        Long memberId,
        LocalDate date,
        Long timeId,
        Long themeId
) {
}
