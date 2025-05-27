package roomescape.reservation.application.dto;

import java.time.LocalDate;

public interface ReservationRequest {
    LocalDate date();

    Long timeId();

    Long themeId();
}
