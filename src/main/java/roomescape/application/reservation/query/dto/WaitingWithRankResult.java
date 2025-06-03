package roomescape.application.reservation.query.dto;

import roomescape.domain.reservation.Waiting;

import java.time.LocalDate;
import java.time.LocalTime;

public record WaitingWithRankResult(
        Long waitingId,
        String themeName,
        LocalDate reservationDate,
        LocalTime reservationTime,
        Long waitingCount
) {

    public static WaitingWithRankResult from(final Waiting waiting, final Long waitingCount) {
        return new WaitingWithRankResult(
                waiting.getId(),
                waiting.getTheme().getName(),
                waiting.getDate(),
                waiting.getTime().getStartAt(),
                waitingCount
        );
    }
}
