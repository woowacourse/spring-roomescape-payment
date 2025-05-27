package roomescape.reservation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import roomescape.reservation.entity.Payment;
import roomescape.reservation.entity.Reservation;

public record ReservationCreateRequest(
        @NotNull LocalDate date,
        @NotNull Long timeId,
        @NotNull Long themeId,
        @NotNull String paymentKey,
        @NotNull String orderId,
        @NotNull Long amount,
        @NotNull String paymentType
) {

    public Payment toPayment() {
        return new Payment(paymentKey, orderId, amount, paymentType);
    }
}
