package roomescape.reservation.dto.response;

import roomescape.reservation.model.ReservationWithPayment;

import java.time.LocalDate;
import java.time.LocalTime;

public record FindReservationWithPaymentResponse(Long id,
                                                 String theme,
                                                 LocalDate date,
                                                 LocalTime time,
                                                 String status,
                                                 String paymentKey,
                                                 Long totalAmount) {
    public static FindReservationWithPaymentResponse from(final ReservationWithPayment reservationWithPayment) {
        if (reservationWithPayment.payment() != null) {
            return new FindReservationWithPaymentResponse(
                    reservationWithPayment.reservation().getId(),
                    reservationWithPayment.reservation().getTheme().getName(),
                    reservationWithPayment.reservation().getDate(),
                    reservationWithPayment.reservation().getReservationTime().getStartAt(),
                    "예약",
                    reservationWithPayment.payment().getPaymentKey(),
                    reservationWithPayment.payment().getTotalAmount()
            );
        }
        return new FindReservationWithPaymentResponse(
                reservationWithPayment.reservation().getId(),
                reservationWithPayment.reservation().getTheme().getName(),
                reservationWithPayment.reservation().getDate(),
                reservationWithPayment.reservation().getReservationTime().getStartAt(),
                "예약",
                null,
                null
        );
    }
}
