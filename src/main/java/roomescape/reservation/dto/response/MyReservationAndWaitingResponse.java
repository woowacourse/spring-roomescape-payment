package roomescape.reservation.dto.response;

import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.waiting.domain.Waiting;

import java.time.LocalDate;
import java.time.LocalTime;

public record MyReservationAndWaitingResponse(Long id,
                                              String theme,
                                              LocalDate date,
                                              LocalTime time,
                                              String status,
                                              String paymentKey,
                                              Long amount) {

    public static MyReservationAndWaitingResponse from(Reservation reservation) {
        if (reservation.getReservationStatus() != ReservationStatus.PENDING) {
            return new MyReservationAndWaitingResponse(
                    reservation.getId(),
                    reservation.getThemeName(),
                    reservation.getDate(),
                    reservation.getReservationTime(),
                    reservation.getReservationStatus().getStatus(),
                    reservation.getPayment().getPaymentKey(),
                    reservation.getPayment().getAmount()
            );
        }

        return new MyReservationAndWaitingResponse(
                reservation.getId(),
                reservation.getThemeName(),
                reservation.getDate(),
                reservation.getReservationTime(),
                reservation.getReservationStatus().getStatus(),
                null,
                null
        );
    }

    public static MyReservationAndWaitingResponse fromWaiting(Waiting waiting, long rank) {
        return new MyReservationAndWaitingResponse(
                waiting.getId(),
                waiting.getTheme().getName(),
                waiting.getDate(),
                waiting.getTime().getStartAt(),
                String.valueOf(rank),
                null,
                null);
    }
}
