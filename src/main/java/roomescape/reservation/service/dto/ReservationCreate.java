package roomescape.reservation.service.dto;

import java.time.LocalDate;

public record ReservationCreate(long timeId, long themeId, long memberId, LocalDate date) {
}
