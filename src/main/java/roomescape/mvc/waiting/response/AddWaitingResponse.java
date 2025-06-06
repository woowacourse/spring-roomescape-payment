package roomescape.mvc.waiting.response;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.mvc.waiting.domain.Waiting;

public record AddWaitingResponse(
        Long id,
        LocalDate date,
        String memberName,
        String themeName,
        LocalTime startAt
) {

    public AddWaitingResponse(Waiting waiting) {
        this(
                waiting.getId(),
                waiting.getDate(),
                waiting.getMember().getName(),
                waiting.getTheme().getName(),
                waiting.getTime().getStartAt()
        );
    }
}
