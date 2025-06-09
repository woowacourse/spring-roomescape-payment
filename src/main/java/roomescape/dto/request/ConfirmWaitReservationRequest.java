package roomescape.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ConfirmWaitReservationRequest(
        @NotBlank(message = "payment key 는 필수입니다.") String paymentKey,
        @NotBlank(message = "order ID 는 필수입니다.") String orderId,
        @Positive(message = "금액은 음수가 될 수 없습니다.") Integer amount,
        @NotBlank(message = "결제 유형은 필수입니다.") String paymentType) {
}
