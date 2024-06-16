package roomescape.waiting.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.Reservation;
import roomescape.waiting.domain.Waiting;

@Tag(name = "예약 대기 응답", description = "예약 대기 결과를 사용자에게 응답한다.")
public record WaitingResponse(
        long id,
        String memberName,
        String themeName,
        LocalDate date,
        LocalTime startAt
) {
    public static WaitingResponse from(Waiting waiting) {
        Reservation reservation = waiting.getReservation();

        return new WaitingResponse(
                waiting.getId(),
                waiting.getMember().getName(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getReservationTime().getStartAt()
        );
    }
}
