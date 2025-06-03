package roomescape.booking.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReservationPaymentRequest(
        @NotNull LocalDate date,
        @NotNull Long themeId,
        @NotNull Long timeId,
        @NotBlank String paymentKey,
        @NotNull String orderId,
        @NotNull Long amount,
        @NotBlank String paymentType
) {
}
