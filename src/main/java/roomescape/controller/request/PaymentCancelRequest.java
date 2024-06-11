package roomescape.controller.request;

import jakarta.validation.constraints.NotNull;

public record PaymentCancelRequest(
        @NotNull
        String cancelReason) {
}
