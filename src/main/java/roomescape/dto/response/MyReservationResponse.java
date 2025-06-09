package roomescape.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.ReservationWithRank;
import roomescape.entity.Payment;
import roomescape.entity.Reservation;

public record MyReservationResponse(Long id,
                                    String theme,
                                    LocalDate date,
                                    LocalTime time,
                                    String status,
                                    String paymentKey,
                                    Integer amount) {

    public static MyReservationResponse from(ReservationWithRank reservationWithRank) {

        Reservation reservation = reservationWithRank.getReservation();
        Payment payment = reservationWithRank.getReservation().getPayment();

        if (payment == null) {
            return new MyReservationResponse(
                    reservation.getId(),
                    reservation.getThemeName(),
                    reservation.getDate(),
                    reservation.getStartAt(),
                    reservation.getStatus().renderText(reservationWithRank.getRank()),
                    null,
                    null
            );
        }

        return new MyReservationResponse(
                reservation.getId(),
                reservation.getThemeName(),
                reservation.getDate(),
                reservation.getStartAt(),
                reservation.getStatus().renderText(reservationWithRank.getRank()),
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }
}
