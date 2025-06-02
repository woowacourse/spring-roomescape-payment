package roomescape.reservation.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import roomescape.reservation.domain.Amount;
import roomescape.reservation.domain.OrderId;
import roomescape.reservation.domain.PaymentInfo;
import roomescape.reservation.domain.PaymentKey;

public record CreateReservationRequest(
        @NotNull LocalDate date,
        @NotNull Long timeId,
        @NotNull Long themeId,
        @NotBlank String orderId,
        @NotNull Long amount,
        @NotBlank String paymentKey
) {
    public PaymentInfo toPaymentInfo() {
        return new PaymentInfo(
                new OrderId(orderId),
                new Amount(amount),
                new PaymentKey(paymentKey)
        );
    }
}
