package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservationWithPaymentRequest(
        @Schema(description = "예약 요청 날짜") LocalDate date,
        @Schema(description = "예약 요청 시간") long timeId,
        @Schema(description = "예약 요청 테마") long themeId,
        @Schema(description = "결제 키") String paymentKey,
        @Schema(description = "주문 번호") String orderId,
        @Schema(description = "금액") BigDecimal amount
) {
    public Payment toPayment() {
        return new Payment(paymentKey, orderId, amount);
    }
}
