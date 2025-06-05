package roomescape.reservation.dto.response;

import java.time.LocalDate;
import roomescape.member.dto.response.MemberResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.Waiting;
import roomescape.theme.dto.response.ThemeResponse;
import roomescape.timeslot.dto.response.TimeSlotResponse;

public record ReservationResponse(Long id, LocalDate date, TimeSlotResponse time, ThemeResponse theme,
                                  MemberResponse member) {
    public static ReservationResponse from(final Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getDate(),
                new TimeSlotResponse(
                        reservation.getTime().getId(),
                        reservation.getTime().getStartAt()
                ),
                new ThemeResponse(reservation.getTheme().getId(),
                        reservation.getTheme().getName(),
                        reservation.getTheme().getDescription(),
                        reservation.getTheme().getThumbnail()
                ),
                new MemberResponse(reservation.getMember().getId(),
                        reservation.getMember().getName())
        );
    }

    public static ReservationResponse from(final Waiting waiting) {
        Reservation reservation = waiting.getReservation();
        return new ReservationResponse(
                waiting.getId(),
                reservation.getDate(),
                new TimeSlotResponse(
                        reservation.getTime().getId(),
                        reservation.getTime().getStartAt()
                ),
                new ThemeResponse(reservation.getTheme().getId(),
                        reservation.getTheme().getName(),
                        reservation.getTheme().getDescription(),
                        reservation.getTheme().getThumbnail()
                ),
                new MemberResponse(waiting.getMember().getId(),
                        waiting.getMember().getName())
        );
    }
}
