package roomescape.presentation.request;

import jakarta.validation.constraints.NotNull;

public record ReservationPaymentRequest(
    @NotNull
    Long reservationId,

    @NotNull
    String paymentKey,

    @NotNull
    String orderId,

    @NotNull
    Integer amount
) {

}
