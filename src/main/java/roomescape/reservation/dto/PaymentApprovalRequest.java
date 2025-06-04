package roomescape.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

public record PaymentApprovalRequest(
        @NotBlank String paymentKey,
        @NotBlank String orderId,
        @Range(min = 1000L) long amount
) {
}
