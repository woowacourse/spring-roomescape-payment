package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;

public record MyReservationResponse(
        Long reservationId,
        String theme,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        String status,
        Long rank,
        String paymentKey,
        Long amount
) {

    public MyReservationResponse(final Reservation reservation, final Payment payment, final ReservationStatus status) {
        this(
                reservation.getId(),
                reservation.getRoomEscapeInformation().getTheme().getName(),
                reservation.getRoomEscapeInformation().getDate(),
                reservation.getRoomEscapeInformation().getTime().getStartAt(),
                status.getOutput(),
                null,
                payment == null ? null : payment.getPaymentKey(),
                payment == null ? null : payment.getAmount()
        );
    }

    public MyReservationResponse(final WaitingReservationWithRank waitingReservationWithRank,
                                 final ReservationStatus status) {
        this(
                waitingReservationWithRank.reservationId(),
                waitingReservationWithRank.theme(),
                waitingReservationWithRank.date(),
                waitingReservationWithRank.time(),
                status.getOutput(),
                waitingReservationWithRank.rank(),
                null,
                null
        );
    }

    public static MyReservationResponse from(final Reservation reservation, final Payment payment) {
        return new MyReservationResponse(reservation, payment, ReservationStatus.BOOKED);
    }

    public static MyReservationResponse from(WaitingReservationWithRank waiting) {
        return new MyReservationResponse(waiting, ReservationStatus.WAITING);
    }
}
