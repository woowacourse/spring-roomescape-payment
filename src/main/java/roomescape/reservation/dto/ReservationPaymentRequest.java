package roomescape.reservation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.payment.dto.PaymentRequest;

@Schema(description = "예약 결제 요청")
public record ReservationPaymentRequest(
        @Schema(description = "예약 날짜", defaultValue = "#{T(java.time.LocalDate).now()}")
        LocalDate date,
        @Schema(description = "테마 ID", defaultValue = "1")
        long themeId,
        @Schema(description = "예약 시간 ID", defaultValue = "1")
        long timeId,
        @Schema(description = "PaymentKey", defaultValue = "paymentKey")
        String paymentKey,
        @Schema(description = "OrderId", defaultValue = "orderId")
        String orderId,
        @Schema(description = "Amount", defaultValue = "1000")
        BigDecimal amount) {

    public PaymentRequest toPaymentRequest() {
        return new PaymentRequest(paymentKey, orderId, amount);
    }

    public ReservationRequest toReservationRequest() {
        return new ReservationRequest(date, timeId, themeId);
    }
}