package roomescape.service.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import roomescape.domain.payment.PaymentType;
import roomescape.service.payment.dto.PaymentRequest;

public record ReservationRequest(
        LocalDate date,
        @NotNull(message = "시간 ID를 입력해주세요.") Long timeId,
        @NotNull(message = "테마 ID를 입력해주세요.") Long themeId,
        @NotBlank(message = "결제 키가 유효하지 않습니다.") String paymentKey,
        @NotBlank(message = "주문 번호가 유효하지 않습니다.") String orderId,
        @PositiveOrZero(message = "결제 금액은 음수일 수 없습니다.") BigDecimal amount,
        @NotBlank(message = "결제 수단이 유효하지 않습니다.") String paymentType
) {

    public static ReservationRequest fromAdminRequest(AdminReservationRequest request) {
        return new ReservationRequest(
                request.date(), request.timeId(), request.themeId(),
                "PAID_BY_ADMIN", "ORDER_BY_ADMIN", BigDecimal.ZERO, PaymentType.ADMIN.name()
        );
    }

    public PaymentRequest toPaymentRequest() {
        return new PaymentRequest(paymentKey, orderId, amount, PaymentType.valueOf(paymentType));
    }
}
