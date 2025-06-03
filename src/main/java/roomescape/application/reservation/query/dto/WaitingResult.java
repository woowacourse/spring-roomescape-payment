package roomescape.application.reservation.query.dto;

import roomescape.domain.reservation.Waiting;

import java.time.LocalDate;
import java.time.LocalTime;

public record WaitingResult(
        Long waitingId,
        String memberName,
        String themeName,
        LocalDate reservationDate,
        LocalTime reservationTime
) {

    public static WaitingResult from(final Waiting waiting) {
        return new WaitingResult(
                waiting.getId(),
                waiting.getMember().getName(),
                waiting.getTheme().getName(),
                waiting.getDate(),
                waiting.getTime().getStartAt()
        );
    }
}
