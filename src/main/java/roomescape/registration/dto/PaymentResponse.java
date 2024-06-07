package roomescape.registration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.payment.domain.Payment;

import java.time.LocalDateTime;

@Schema(description = "결제 응답")
public record PaymentResponse(

        @Schema(description = "결제 ID", example = "123")
        Long id,

        @Schema(description = "생성 일자 및 시간", example = "2099-12-31 23:59")
        LocalDateTime createdAt,

        @Schema(description = "결제 금액", example = "50000")
        Long amount) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getCreatedAt(),
                payment.getAmount()
        );
    }

    public static PaymentResponse getPaymentResponseForNotPaidReservation() {
        return new PaymentResponse(
                null, null, null
        );
    }
}
