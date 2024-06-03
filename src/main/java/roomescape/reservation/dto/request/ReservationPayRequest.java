package roomescape.reservation.dto.request;

import jakarta.validation.Valid;
import roomescape.payment.dto.request.PaymentConfirmRequest;

public record ReservationPayRequest(
        @Valid ReservationSaveRequest reservationSaveRequest,
        @Valid PaymentConfirmRequest paymentConfirmRequest
) {
}
