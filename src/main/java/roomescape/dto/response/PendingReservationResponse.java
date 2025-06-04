package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.reservation.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;

public record PendingReservationResponse(
        @Schema(example = "1")
        long id,

        PendingReservationMemberSlot member,

        PendingReservationThemeSlot theme,

        PendingReservationTimeSlot time,

        @Schema(example = "2025-06-05")
        LocalDate date
) {

    public record PendingReservationMemberSlot(
            @Schema(example = "1")
            long memberId,

            @Schema(example = "돔푸")
            String name
    ) {
    }

    public record PendingReservationThemeSlot(
            @Schema(example = "1")
            long themeId,

            @Schema(example = "주홍색 연구")
            String themeName
    ) {
    }

    public record PendingReservationTimeSlot(
            @Schema(example = "1")
            long timeId,

            @Schema(example = "15:00")
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
