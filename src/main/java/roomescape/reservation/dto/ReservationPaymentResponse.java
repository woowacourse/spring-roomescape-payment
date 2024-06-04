package roomescape.reservation.dto;

import roomescape.payment.dto.PaymentResponse;

public record ReservationPaymentResponse(ReservationResponse reservationResponse, PaymentResponse paymentResponse) {
}
