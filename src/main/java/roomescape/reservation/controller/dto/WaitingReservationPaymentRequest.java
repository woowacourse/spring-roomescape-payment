package roomescape.reservation.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record WaitingReservationPaymentRequest(
        @NotBlank
        @Pattern(regexp = "^(?:(?:19|20)\\d{2})-(?:0[1-9]|1[0-2])-(?:0[1-9]|[1-2][0-9]|3[0-1])$")
        String date,

        @NotNull
        String time,

        @NotNull
        String theme,

        @NotNull
        String paymentKey,

        @NotNull
        String orderId,

        @NotNull
        Long amount,

        @NotNull
        String paymentType
) {
}
