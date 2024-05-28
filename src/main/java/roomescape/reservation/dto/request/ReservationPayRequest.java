package roomescape.reservation.dto.request;

import roomescape.payment.dto.PaymentConfirmRequest;

public record ReservationPayRequest(
        ReservationSaveRequest reservationSaveRequest,
        PaymentConfirmRequest paymentConfirmRequest
) {
}
