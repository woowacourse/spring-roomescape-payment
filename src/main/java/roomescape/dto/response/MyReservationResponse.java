package roomescape.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.ReservationDetail;
import roomescape.domain.ReservationWithRank;
import roomescape.entity.Payment;
import roomescape.entity.Reservation;

public record MyReservationResponse(
        Long id,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        String paymentKey,
        int amount
) {

    public static MyReservationResponse from(ReservationDetail reservationDetail) {

        Payment payment = reservationDetail.getPayment();
        Reservation reservation = reservationDetail.getReservation();


        return new MyReservationResponse(
                reservation.getId(),
                reservation.getThemeName(),
                reservation.getDate(),
                reservation.getStartAt(),
                reservation.getStatus().renderText(reservationDetail.getRank()),
                payment != null ? payment.getPaymentKey() : null,
                payment != null ? payment.getAmount() : 0
        );
    }
}
