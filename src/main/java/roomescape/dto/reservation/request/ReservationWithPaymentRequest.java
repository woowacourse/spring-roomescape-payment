package roomescape.dto.reservation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationWithPaymentRequest(
        @NotNull(message = "예약 날짜는 필수입니다.") LocalDate date,
        @NotNull(message = "예약 시간 번호는 필수입니다.") Long timeId,
        @NotNull(message = "예약 테마 번호는 필수입니다.") Long themeId,
        @NotBlank(message = "페이먼트 키는 비어 있을 수 없습니다.") String paymentKey,
        @NotBlank(message = "주문 번호는 비어 있을 수 없습니다.") String orderId,
        @NotNull(message = "결제 금액은 필수입니다.") int amount
) {
}
