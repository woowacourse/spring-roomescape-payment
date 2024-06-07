package roomescape.reservation.dto.response;

import roomescape.reservation.model.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;

public record FindReservationWithPaymentResponse(Long id,
                                                 String theme,
                                                 LocalDate date,
                                                 LocalTime time,
                                                 String status,
                                                 String paymentKey,
                                                 Long totalAmount) {
    public static FindReservationWithPaymentResponse from(final Reservation reservation) {
        return new FindReservationWithPaymentResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getReservationTime().getStartAt(),
                "예약",
                reservation.getPayment().getPaymentKey(),
                reservation.getPayment().getTotalAmount()
        );
    }
}
