package roomescape.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BookedPaymentRequest(

        @NotNull
        @Positive
        Long id,

        @NotBlank
        String paymentKey,

        @NotBlank
        String orderId,

        @NotNull
        @Positive
        Integer amount
) {
}
