package roomescape.controller.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReservationWithPaymentRequest(
        @NotNull
        @Positive(message = "[ERROR] reservationId의 값이 1보다 작을 수 없습니다.")
        Long reservationId,
        @NotEmpty
        String orderId,
        @NotEmpty
        String paymentKey,
        @NotNull
        Long amount) {
}
