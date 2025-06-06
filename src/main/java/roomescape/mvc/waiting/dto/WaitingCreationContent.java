package roomescape.mvc.waiting.dto;

import java.time.LocalDate;

public record WaitingCreationContent(
        LocalDate date,
        Long themeId,
        Long timeId,
        Long memberId
) {

}
