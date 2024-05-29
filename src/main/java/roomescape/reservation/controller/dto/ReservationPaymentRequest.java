package roomescape.reservation.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ReservationPaymentRequest(
        @NotNull(message = "멤버 예약 id는 필수 값입니다.")
        @Positive
        Long memberReservationId,
        @NotBlank(message = "paymentKey는 필수 값입니다.")
        String paymentKey,

        @NotBlank(message = "주문 id는 필수 값입니다.")
        String orderId,

        @NotNull(message = "결제 금액은 필수 값입니다.")
        @Positive
        BigDecimal amount
) {
}
