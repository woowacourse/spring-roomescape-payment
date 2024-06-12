package roomescape.controller.dto.response;

import java.util.Optional;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.reservation.Payment;

public record PaymentResponse(
        @Schema(description = "결제 정보 여부", example = "true")
        boolean isExists,
        @Schema(description = "결제 고유 번호", example = "1")
        Long id,
        @Schema(description = "주문 번호", example = "ORDERde4332mdberk24f")
        String orderId,
        @Schema(description = "가격", example = "10000")
        long amount,
        @Schema(description = "결제 키", example = "dsfoie351lksa033")
        String paymentKey
) {
    public static PaymentResponse EMPTY = new PaymentResponse(false, null, "", 0, "");

    public static PaymentResponse from(Optional<Payment> optional) {
        if (optional.isEmpty()) {
            return EMPTY;
        }

        Payment payment = optional.get();
        return new PaymentResponse(
                true,
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getPaymentKey()
        );
    }
}
