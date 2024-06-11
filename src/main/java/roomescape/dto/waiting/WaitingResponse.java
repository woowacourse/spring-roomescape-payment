package roomescape.dto.waiting;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.waiting.Waiting;

public record WaitingResponse(
        Long waitingId,
        String member,
        String theme,
        LocalDate date,
        LocalTime startAt,
        int order
) {

    public static WaitingResponse from(Waiting waiting) {
        Reservation reservation = waiting.getReservation();
        return new WaitingResponse(
                reservation.getId(),
                reservation.getMember().getName(),
                reservation.getTheme().getThemeName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                waiting.getWaitingOrderValue()
        );
    }
}
