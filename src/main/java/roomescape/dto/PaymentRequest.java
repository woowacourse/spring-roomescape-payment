package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 요청 DTO")
public record PaymentRequest(@Schema(description = "결제 key", example = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm") String paymentKey,
                             @Schema(description = "주문 id", example = "orderId") String orderId,
                             @Schema(description = "결제 가격", example = "1000", type = "long") long amount) {
    public static PaymentRequest from(ReservationWithPaymentRequest reservationWithPaymentRequest) {
        return new PaymentRequest(reservationWithPaymentRequest.paymentKey(),
                reservationWithPaymentRequest.orderId(),
                reservationWithPaymentRequest.amount());
    }
}
