package roomescape.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.Waiting;

public record WaitingResponse(Long id, String name, String theme, LocalDate date, LocalTime startAt) {

    public static WaitingResponse from(Waiting waiting) {
        return new WaitingResponse(
            waiting.getId(),
            waiting.getMember().getName(),
            waiting.getReservation().getTheme().getName(),
            waiting.getReservation().getDate(),
            waiting.getReservation().getTime().getStartAt()
        );
    }
}
