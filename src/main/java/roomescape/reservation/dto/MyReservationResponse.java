package roomescape.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.payment.dto.PaymentHistoryDto;

public record MyReservationResponse(
        Long reservationId,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        String paymentStatus,
        String paymentKey,
        Long totalAmount
) {

    public static MyReservationResponse from(final ReservationDto reservation, final PaymentHistoryDto paymentHistory) {
        return new MyReservationResponse(
                reservation.id(),
                reservation.theme().name().getValue(),
                reservation.date().getValue(),
                reservation.time().startAt(),
                reservation.status().getDescription(),
                reservation.paymentStatus().getDescription(),
                paymentHistory.paymentKey(),
                paymentHistory.totalAmount()
        );
    }
}
