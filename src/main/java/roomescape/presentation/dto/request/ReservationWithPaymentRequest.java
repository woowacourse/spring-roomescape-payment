package roomescape.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "예약 및 결제 요청 DTO")
public record ReservationWithPaymentRequest(
        @Schema(description = "예약 일자", example = "2024-03-20")
        @NotNull(message = "예약 일자는 필수입니다.")
        LocalDate date,

        @Schema(description = "예약 시간 ID", example = "1")
        @NotNull(message = "예약 시간은 필수입니다.")
        Long timeId,

        @Schema(description = "예약 테마 ID", example = "1")
        @NotNull(message = "예약 테마는 필수입니다.")
        Long themeId,

        @Schema(description = "결제 키", example = "tosspayments_123456789")
        @NotNull(message = "결제 키는 필수입니다.")
        String paymentKey,

        @Schema(description = "주문 번호", example = "order_123456789")
        @NotNull(message = "주문 번호는 필수입니다.")
        String orderId,

        @Schema(description = "결제 금액", example = "30000")
        @NotNull(message = "결제 금액은 필수입니다.")
        String amount
) {
    public PaymentProcessRequest toPaymentProcessRequest() {
        return new PaymentProcessRequest(paymentKey, orderId, amount);
    }
}
