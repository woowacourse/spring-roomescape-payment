package roomescape.reservation.dto.response;

import jakarta.validation.constraints.NotNull;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;

public record ReservationWithPayment(
        @NotNull Reservation reservation,
        @NotNull Payment payment
) {
}
