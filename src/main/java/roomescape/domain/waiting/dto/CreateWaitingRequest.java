package roomescape.domain.waiting.dto;

import java.time.LocalDate;

public record CreateWaitingRequest(
        LocalDate date,
        Long theme,
        Long time
) {
}
