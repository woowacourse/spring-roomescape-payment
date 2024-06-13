package roomescape.reservation.dto.request;

import jakarta.validation.Valid;
import roomescape.payment.application.ProductPayRequest;

public record ReservationPayRequest(
        @Valid ReservationSaveRequest reservationSaveRequest,
        @Valid ProductPayRequest productPayRequest
) {
}
