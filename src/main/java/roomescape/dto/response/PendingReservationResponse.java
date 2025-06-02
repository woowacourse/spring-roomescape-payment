package roomescape.dto.response;

import roomescape.domain.reservation.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;

public record PendingReservationResponse(
        Long id,
        PendingReservationMemberSlot member,
        PendingReservationThemeSlot theme,
        PendingReservationTimeSlot time,
        LocalDate date
) {

    public record PendingReservationMemberSlot(
            Long memberId,
            String name
    ) {
    }

    public record PendingReservationThemeSlot(
            Long themeId,
            String themeName
    ) {
    }

    public record PendingReservationTimeSlot(
            Long timeId,
            LocalTime startAt
    ) {
    }

    public static PendingReservationResponse from(Reservation reservation) {
        return new PendingReservationResponse(
                reservation.getId(),
                new PendingReservationMemberSlot(
                        reservation.getMember().getId(),
                        reservation.getMember().getName()
                ),
                new PendingReservationThemeSlot(
                        reservation.getReservationItem().getTheme().getId(),
                        reservation.getReservationItem().getTheme().getName()
                ),
                new PendingReservationTimeSlot(
                        reservation.getReservationItem().getTime().getId(),
                        reservation.getReservationItem().getTime().getStartAt()
                ),
                reservation.getReservationItem().getDate()
        );
    }
}
