package roomescape.application.dto.response.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.ReservationWithRank;

public record UserReservationResponse(
        Long reservationId,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        Long rank,
        String paymentKey,
        Long amount
) {

    public static UserReservationResponse from(ReservationWithRank reservation) {
        return new UserReservationResponse(
                reservation.reservation().getId(),
                reservation.reservation().getDetail().getTheme().getName(),
                reservation.reservation().getDetail().getDate(),
                reservation.reservation().getDetail().getTime().getStartAt(),
                reservation.reservation().getStatus().name(),
                reservation.rank(),
                getPayment(reservation),
                getAmount(reservation));
    }

    private static String getPayment(ReservationWithRank reservation) {
        if (reservation.isPaid()) {
            return reservation.reservation().getPayment().getPaymentKey();
        }
        return "";
    }

    private static Long getAmount(ReservationWithRank reservation) {
        if (reservation.isPaid()) {
            return reservation.reservation().getPayment().getAmount();
        }
        return 0L;
    }
}
