package roomescape.dto.reservation;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "예약 요청")
public record ReservationSaveRequest(

        @Schema(description = "예약 날짜", example = "2024-06-01")
        @Future
        @NotNull
        LocalDate date,

        @Schema(description = "시간 ID", example = "1")
        @Positive
        @NotNull
        Long timeId,

        @Schema(description = "테마 ID", example = "1")
        @Positive
        @NotNull
        Long themeId,

        @Schema(description = "결제 키", example = "payment_key_1234")
        @NotNull
        String paymentKey,

        @Schema(description = "주문 ID", example = "order_id_1234")
        @NotNull
        String orderId,

        @Schema(description = "결제 금액", example = "1000")
        @Positive
        @NotNull
        Long amount
) {
}
