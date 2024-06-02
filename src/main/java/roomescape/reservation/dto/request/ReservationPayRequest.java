package roomescape.reservation.dto.request;

import jakarta.validation.Valid;
import roomescape.payment.dto.PaymentConfirmRequest;

public record ReservationPayRequest(
        @Valid ReservationSaveRequest reservationSaveRequest,
        @Valid PaymentConfirmRequest paymentConfirmRequest
) {
}
