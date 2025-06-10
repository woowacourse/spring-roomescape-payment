package roomescape.dto.reservation.response;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.Waiting;

public record WaitingResponse(Long id, String name, String theme, LocalDate date, LocalTime startAt) {

    public static WaitingResponse from(Waiting waiting) {
        return new WaitingResponse(
                waiting.id(),
                waiting.member().getName(),
                waiting.reservation().theme().name(),
                waiting.reservation().date(),
                waiting.reservation().time().startAt()
        );
    }
}
