package roomescape.domain.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record MyReservationResponse(
        Long reservationId,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        String paymentKey,
        Long amount
) {
    public static MyReservationResponse from(final ReservationWithPaymentHistoryDto reservationWithPaymentHistoryDto) {
        return new MyReservationResponse(
                reservationWithPaymentHistoryDto.id(),
                reservationWithPaymentHistoryDto.theme().name().getValue(),
                reservationWithPaymentHistoryDto.date().getValue(),
                reservationWithPaymentHistoryDto.time().startAt(),
                reservationWithPaymentHistoryDto.status().getDescription(),
                reservationWithPaymentHistoryDto.paymentKey(),
                reservationWithPaymentHistoryDto.totalAmount()
        );
    }
}
