package roomescape.reservation.dto.request;

import jakarta.validation.Valid;
import roomescape.payment.domain.NewPayment;

public record ReservationPayRequest(
        @Valid ReservationSaveRequest reservationSaveRequest,
        @Valid PaymentConfirmRequest paymentConfirmRequest
) {

    public NewPayment newPayment() {
        return paymentConfirmRequest.toNewPayment();
    }
}
