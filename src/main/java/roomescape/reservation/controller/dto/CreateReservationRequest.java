package roomescape.reservation.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import roomescape.reservation.domain.Amount;
import roomescape.reservation.domain.OrderId;
import roomescape.reservation.domain.PaymentInfo;
import roomescape.reservation.domain.PaymentKey;

@Schema(description = "사용자가 예약을 생성할 때 전달하는 요청 정보")
public record CreateReservationRequest(

        @Schema(description = "예약 날짜", example = "2025-07-01")
        @NotNull
        LocalDate date,

        @Schema(description = "예약 시간 ID", example = "3")
        @NotNull
        Long timeId,

        @Schema(description = "테마 ID", example = "5")
        @NotNull
        Long themeId,

        @Schema(description = "결제 주문 ID", example = "ORD20250608-0001")
        @NotBlank
        String orderId,

        @Schema(description = "결제 금액", example = "22000")
        @NotNull
        Long amount,

        @Schema(description = "결제 키", example = "test_payment_key_abcdefg")
        @NotBlank
        String paymentKey

) {
    public PaymentInfo toPaymentInfo() {
        return new PaymentInfo(
                new OrderId(orderId),
                new Amount(amount),
                new PaymentKey(paymentKey)
        );
    }
}
