package roomescape.dto.reservation;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "예약 요청")
public record ReservationWithPaymentRequest(

        @Schema(description = "예약 날짜", example = "2024-06-01", requiredMode = REQUIRED)
        LocalDate date,

        @Schema(description = "시간 ID", example = "1", requiredMode = REQUIRED)
        Long timeId,

        @Schema(description = "테마 ID", example = "1", requiredMode = REQUIRED)
        Long themeId,

        @Schema(description = "결제 키", example = "payment_key_1234", requiredMode = REQUIRED)
        String paymentKey,

        @Schema(description = "주문 ID", example = "order_id_1234", requiredMode = REQUIRED)
        String orderId,

        @Schema(description = "결제 금액", example = "1000", requiredMode = REQUIRED)
        Long amount
) {
}
