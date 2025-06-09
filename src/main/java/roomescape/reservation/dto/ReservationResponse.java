package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;
import roomescape.member.dto.MemberResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.WaitingReservation;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.theme.dto.ThemeResponse;

public record ReservationResponse(
        Long id,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme,
        MemberResponse member
) {

    public ReservationResponse(final Reservation reservation) {
        this(
                reservation.getId(),
                reservation.getDate(),
                new ReservationTimeResponse(reservation.getTime()),
                new ThemeResponse(reservation.getTheme()),
                new MemberResponse(reservation.getMember())
        );
    }

    public static List<ReservationResponse> fromReservations(final List<Reservation> reservations) {
        return reservations.stream()
                .map(ReservationResponse::new)
                .toList();
    }

    public ReservationResponse(final WaitingReservation waitingReservation) {
        this(
                waitingReservation.getId(),
                waitingReservation.getDate(),
                new ReservationTimeResponse(waitingReservation.getTime()),
                new ThemeResponse(waitingReservation.getTheme()),
                new MemberResponse(waitingReservation.getMember())
        );
    }

    public static List<ReservationResponse> fromWaitings(final List<WaitingReservation> waitingReservations) {
        return waitingReservations.stream()
                .map(ReservationResponse::new)
                .toList();
    }
}
