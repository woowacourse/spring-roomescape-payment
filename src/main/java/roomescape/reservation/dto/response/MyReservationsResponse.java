package roomescape.reservation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.ReservationWithPayment;
import roomescape.reservation.waiting.domain.WaitingWithRank;

public record MyReservationsResponse(
        Long id,
        String theme,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm")
        LocalTime time,
        String status,
        Long amount,
        String paymentKey
) {

    public static MyReservationsResponse from(final ReservationWithPayment reservationWithPayment) {
        return new MyReservationsResponse(
                reservationWithPayment.getReservation().getId(),
                reservationWithPayment.getReservation().themeName(),
                reservationWithPayment.getReservation().getDate(),
                reservationWithPayment.getReservation().startTime(),
                reservationWithPayment.getReservation().statusDescription(),
                reservationWithPayment.getPayment().getAmount(),
                reservationWithPayment.getPayment().getPaymentKey()
        );
    }

    public static MyReservationsResponse from(final WaitingWithRank waitingWithRank) {
        return new MyReservationsResponse(
                waitingWithRank.getId(),
                waitingWithRank.themeName(),
                waitingWithRank.getDate(),
                waitingWithRank.startTime(),
                String.valueOf(waitingWithRank.getRank()),
                null,
                null
        );
    }
}
