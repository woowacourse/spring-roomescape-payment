package roomescape.payment.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import roomescape.reservation.dto.ReservationRequest;

public record ReservationPaymentRequest(
        @NotNull LocalDate date,
        @NotNull Long themeId,
        @NotNull Long timeId,
        @NotNull String paymentKey,
        @NotNull String orderId,
        @NotNull Long amount,
        @NotNull String paymentType
) {
    public ReservationRequest toReservationRequest() {
        return new ReservationRequest(date, timeId, themeId);
    }
}
