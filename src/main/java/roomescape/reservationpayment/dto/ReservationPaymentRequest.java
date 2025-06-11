package roomescape.reservationpayment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import roomescape.booking.reservation.Reservation;

public record ReservationPaymentRequest(
        @NotBlank String orderId,
        @NotNull Long amount,
        @NotBlank String paymentKey,
        @NotNull Reservation reservation
) {
}
