package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 요청 DTO 입니다.")
public record PaymentRequest(
        @Schema(description = "결제 키 값입니다.")
        String paymentKey,
        @Schema(description = "주문 번호입니다.")
        String orderId,
        @Schema(description = "결제 금액입니다.")
        long amount
) {
    public static PaymentRequest from(ReservationPaymentRequest request){
        return new PaymentRequest(request.paymentKey(), request.orderId(), request.amount());
    }
}
