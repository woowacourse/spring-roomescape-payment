package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.Reservation;

public record MyReservationResponse(
        Long reservationId,
        String theme,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        String status,
        Long rank
) {

    public MyReservationResponse(final Reservation reservation, final ReservationStatusForResponse status) {
        this(
                reservation.getId(),
                reservation.getRoomEscapeInformation().getTheme().getName(),
                reservation.getRoomEscapeInformation().getDate(),
                reservation.getRoomEscapeInformation().getTime().getStartAt(),
                status.getOutput(),
                null
        );
    }

    public MyReservationResponse(final WaitingReservationWithRank waitingReservationWithRank,
                                 ReservationStatusForResponse status) {
        this(
                waitingReservationWithRank.reservationId(),
                waitingReservationWithRank.theme(),
                waitingReservationWithRank.date(),
                waitingReservationWithRank.time(),
                status.getOutput(),
                waitingReservationWithRank.rank()
        );
    }

    public static MyReservationResponse from(Reservation reservation) {
        return new MyReservationResponse(reservation, ReservationStatusForResponse.BOOKED);
    }

    public static MyReservationResponse from(WaitingReservationWithRank waiting) {
        return new MyReservationResponse(waiting, ReservationStatusForResponse.WAITING);
    }
}
