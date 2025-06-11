package roomescape.domain.reservation.dto;

import java.time.LocalDate;
import roomescape.domain.reservation.request.AdminReservationRequest;
import roomescape.domain.reservation.request.ReservationCreationRequest;
import roomescape.domain.waiting.domain.Waiting;

public record ReservationCreationContent(
        Long themeId,
        LocalDate date,
        Long timeId
) {

    public ReservationCreationContent(AdminReservationRequest request) {
        this(request.themeId(), request.date(), request.timeId());
    }

    public ReservationCreationContent(ReservationCreationRequest request) {
        this(request.themeId(), request.date(), request.timeId());
    }

    public ReservationCreationContent(Waiting waiting) {
        this(waiting.getTheme().getId(), waiting.getDate(), waiting.getTime().getId());
    }
}
