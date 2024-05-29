package roomescape.reservation.service.dto;

import java.time.LocalDate;

public record WaitingCreate(Long memberId, LocalDate date, long timeId, long themeId) {
    public ReservationCreate toReservationCreate() {
        return new ReservationCreate(
                timeId,
                themeId,
                memberId,
                date
        );
    }
}
