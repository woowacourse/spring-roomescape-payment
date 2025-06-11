package roomescape.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationCreateRequest(

        @Schema(description = "예약 날짜", example = "2026-01-01")
        @NotNull LocalDate date,

        @Schema(description = "시간 Id", example = "1")
        @NotNull Long timeId,

        @Schema(description = "테마 Id", example = "1")
        @NotNull Long themeId,

        @Schema(description = "외부 결제 paymentKey", example = "tgen_20250607151842Y4Ip9")
        @NotNull String paymentKey,

        @Schema(description = "외부 결제 orderId", example = "WTESTMC4yMTYxNzYzMzY4MjUy")
        @NotNull String orderId,

        @Schema(description = "결제 금액", example = "1000")
        @NotNull Long amount,

        @Schema(description = "외부 결제 paymentType", example = "NORMAL")
        @NotNull String paymentType
) {
}
