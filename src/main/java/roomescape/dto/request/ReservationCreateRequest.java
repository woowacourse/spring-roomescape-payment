package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "예약 생성 요청 객체")
public record ReservationCreateRequest(
        @NotNull
        @Schema(description = "예약 날짜", example = "2023-12-01")
        LocalDate date,

        @NotNull
        @Schema(description = "시간 ID", example = "10")
        Long timeId,

        @NotNull
        @Schema(description = "테마 ID", example = "123")
        Long themeId,

        @NotNull
        @Schema(description = "결제 키")
        String paymentKey,

        @NotNull
        @Schema(description = "주문 ID")
        String orderId,

        @NotNull
        @Schema(description = "결제 금액", example = "1000")
        int amount
) {
}
