package roomescape.service.dto;

import roomescape.domain.reservation.dto.WaitingReadOnly;
import roomescape.domain.reservation.slot.ReservationSlot;

import java.time.LocalDate;
import java.time.LocalTime;

public record WaitingResponse(
        Long id,
        String name,
        String theme,
        LocalDate date,
        LocalTime startAt
) {
    public static WaitingResponse from(WaitingReadOnly waiting) {
        ReservationSlot slot = waiting.slot();
        return new WaitingResponse(
                waiting.id(),
                waiting.member().getName(),
                slot.getTheme().getName(),
                slot.getDate(),
                slot.getTime().getStartAt()
        );
    }
}
