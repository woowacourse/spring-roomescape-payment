package roomescape.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReservationWithPaymentRequest(
        @NotNull
        @Positive(message = "[ERROR] reservationId의 값이 1보다 작을 수 없습니다.")
        @Schema(description = "예약 ID", example = "1")
        Long reservationId,
        @NotEmpty
        @Schema(description = "결제 ID", example = "MOVINPOKE1533")
        String paymentId,
        @NotEmpty
        @Schema(description = "결제 Key", example = "tgen_20240611152237Z1il1")
        String paymentKey,
        @NotNull
        @Schema(description = "결제 금액", example = "1999999")
        Long amount) {
}
