package roomescape.reservation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.waiting.dto.response.WaitingWithRank;

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
                reservationWithPayment.reservation().getId(),
                reservationWithPayment.reservation().themeName(),
                reservationWithPayment.reservation().getDate(),
                reservationWithPayment.reservation().startTime(),
                reservationWithPayment.reservation().statusDescription(),
                reservationWithPayment.payment().getAmount(),
                reservationWithPayment.payment().getPaymentKey()
        );
    }

    public static MyReservationsResponse from(final WaitingWithRank waitingWithRank) {
        return new MyReservationsResponse(
                waitingWithRank.waiting().getId(),
                waitingWithRank.waiting().themeName(),
                waitingWithRank.waiting().getDate(),
                waitingWithRank.waiting().startTime(),
                String.valueOf(waitingWithRank.rank()),
                null,
                null
        );
    }
}
