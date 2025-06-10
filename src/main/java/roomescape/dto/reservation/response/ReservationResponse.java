package roomescape.dto.reservation.response;

import java.time.LocalDate;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Waiting;
import roomescape.dto.member.response.MemberResponse;
import roomescape.dto.theme.response.ThemeResponse;
import roomescape.dto.timeslot.response.TimeSlotResponse;

public record ReservationResponse(Long id, LocalDate date, TimeSlotResponse time, ThemeResponse theme,
                                  MemberResponse member) {

    public static ReservationResponse from(final Reservation reservation) {
        return new ReservationResponse(
                reservation.id(),
                reservation.date(),
                new TimeSlotResponse(
                        reservation.time().id(),
                        reservation.time().startAt()
                ),
                new ThemeResponse(reservation.theme().id(),
                        reservation.theme().name(),
                        reservation.theme().description(),
                        reservation.theme().thumbnail()
                ),
                new MemberResponse(reservation.member().id(),
                        reservation.member().getName())
        );
    }

    public static ReservationResponse from(final Waiting waiting) {
        Reservation reservation = waiting.reservation();
        return new ReservationResponse(
                waiting.id(),
                reservation.date(),
                new TimeSlotResponse(
                        reservation.time().id(),
                        reservation.time().startAt()
                ),
                new ThemeResponse(reservation.theme().id(),
                        reservation.theme().name(),
                        reservation.theme().description(),
                        reservation.theme().thumbnail()
                ),
                new MemberResponse(waiting.member().id(),
                        waiting.member().getName())
        );
    }
}
