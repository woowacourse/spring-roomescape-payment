package roomescape.reservation.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record ReservationCreateRequest(
        @NotNull LocalDate date,
        @NotNull Long themeId,
        @NotNull Long timeId,
        @NotNull String paymentKey,
        @NotNull String orderId,
        @NotNull Long amount,
        @NotNull String paymentType
) {
    public PaymentRequest toPaymentRequest() {
        return new PaymentRequest(amount, orderId, paymentKey);
    }
}
