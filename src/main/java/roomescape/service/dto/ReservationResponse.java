package roomescape.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDate;
import java.time.LocalDateTime;
import roomescape.domain.reservation.BookedMember;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.WaitingMember;

public record ReservationResponse(
        Long id,
        Long reservationId,
        MemberResponse member,
        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme,
        ReservationStatus status
) {

    public static ReservationResponse createByWaiting(WaitingMember waitingMember) {
        Reservation reservation = waitingMember.getReservation();

        return new ReservationResponse(
                waitingMember.getId(),
                reservation.getId(),
                roomescape.service.dto.MemberResponse.from(waitingMember.getMember()),
                reservation.getDate(),
                new ReservationTimeResponse(reservation.getTime()),
                new ThemeResponse(reservation.getTheme()),
                ReservationStatus.WAIT
        );
    }

    public static ReservationResponse createByBooked(BookedMember bookedMember) {
        Reservation reservation = bookedMember.getReservation();

        return new ReservationResponse(
                bookedMember.getId(),
                reservation.getId(),
                roomescape.service.dto.MemberResponse.from(bookedMember.getMember()),
                reservation.getDate(),
                new ReservationTimeResponse(reservation.getTime()),
                new ThemeResponse(reservation.getTheme()),
                ReservationStatus.BOOKED
        );
    }

    public LocalDateTime getDateTime() {
        return LocalDateTime.of(date, time.startAt());
    }
}
