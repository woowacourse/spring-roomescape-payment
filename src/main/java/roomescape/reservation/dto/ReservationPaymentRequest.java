package roomescape.reservation.dto;

import java.time.LocalDate;

import roomescape.payment.dto.PaymentRequest;

public record ReservationPaymentRequest(
        LocalDate date,
        long themeId,
        long timeId,
        String paymentKey,
        String orderId,
        long amount) {
    public PaymentRequest toPaymentRequest() {
        return new PaymentRequest(paymentKey, orderId, amount);
    }

    public ReservationRequest toReservationRequest() {
        return new ReservationRequest(date, timeId, themeId);
    }
}
