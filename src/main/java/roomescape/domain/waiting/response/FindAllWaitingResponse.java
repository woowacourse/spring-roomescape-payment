package roomescape.domain.waiting.response;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.waiting.domain.Waiting;

public record FindAllWaitingResponse(
        Long id,
        LocalDate date,
        String memberName,
        String themeName,
        LocalTime startAt
) {

    public FindAllWaitingResponse(Waiting waiting) {
        this(
                waiting.getId(),
                waiting.getDate(),
                waiting.getMember().getName(),
                waiting.getTheme().getName(),
                waiting.getTime().getStartAt()
        );
    }
}
