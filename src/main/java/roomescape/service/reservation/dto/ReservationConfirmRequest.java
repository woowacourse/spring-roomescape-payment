package roomescape.service.reservation.dto;

import roomescape.domain.dto.PaymentRequest;

public record ReservationConfirmRequest(
        Long reservationId,
        PaymentRequest paymentRequest
) {
}
