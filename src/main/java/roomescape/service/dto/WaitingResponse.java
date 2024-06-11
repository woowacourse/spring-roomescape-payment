package roomescape.service.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.dto.WaitingReadOnly;

public record WaitingResponse(
        Long id,
        String name,
        String theme,
        LocalDate date,
        LocalTime startAt
) {

    public static WaitingResponse from(WaitingReadOnly waiting) {
        return new WaitingResponse(
                waiting.id(),
                waiting.name(),
                waiting.theme(),
                waiting.date(),
                waiting.time()
        );
    }
}
