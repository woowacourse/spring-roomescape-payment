package roomescape.reservation.dto;

import java.time.LocalDate;

public record ReservationPaymentRequest(
        LocalDate date,
        long themeId,
        long timeId,
        String paymentKey,
        String orderId,
        long amount,
        String paymentType
) {
    public static ReservationPaymentRequest from(final AdminReservationPaymentRequest request) {
        return new ReservationPaymentRequest(
                request.date(),
                request.themeId(),
                request.timeId(),
                request.paymentKey(),
                request.orderId(),
                request.amount(),
                request.paymentType()
        );
    }
}
