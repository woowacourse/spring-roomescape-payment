package roomescape.service.response;

import roomescape.domain.reservation.ReservationDate;
import roomescape.domain.reservation.ReservationWaiting;

public record ReservationWaitingDto(
        Long id,
        String name,
        ReservationDate date,
        ReservationTimeDto reservationTimeDto,
        ThemeDto themeDto,
        String status) {

    public ReservationWaitingDto(ReservationWaiting waiting) {
        this(
                waiting.getId(),
                waiting.getMember().getName().getName(),
                waiting.getDate(),
                new ReservationTimeDto(waiting.getTime()),
                new ThemeDto(waiting.getTheme()),
                waiting.getDeniedAt()
        );
    }
}
