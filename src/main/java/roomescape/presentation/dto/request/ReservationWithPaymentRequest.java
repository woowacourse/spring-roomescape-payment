package roomescape.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReservationWithPaymentRequest(
        @NotNull(message = "예약 일자는 필수입니다.")
        LocalDate date,

        @NotNull(message = "예약 시간은 필수입니다.")
        Long timeId,

        @NotNull(message = "예약 테마는 필수입니다.")
        Long themeId,

        @NotNull(message = "결제 키는 필수입니다.")
        String paymentKey,

        @NotNull(message = "주문 번호는 필수입니다.")
        String orderId,

        @NotNull(message = "결제 금액은 필수입니다.")
        String amount
) {

        public PaymentProcessRequest toPaymentProcessRequest() {
                return new PaymentProcessRequest(paymentKey, orderId, amount);
        }
}
