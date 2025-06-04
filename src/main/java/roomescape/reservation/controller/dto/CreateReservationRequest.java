package roomescape.reservation.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import roomescape.reservation.external.toss.TossPaymentRequest;

public record CreateReservationRequest(
        @NotNull LocalDate date,
        @NotNull Long timeId,
        @NotNull Long themeId,
        @NotBlank String orderId,
        @NotNull Long amount,
        @NotBlank String paymentKey
) {
    public TossPaymentRequest toPaymentRequest() {
        return new TossPaymentRequest(orderId, amount, paymentKey);
    }
}
