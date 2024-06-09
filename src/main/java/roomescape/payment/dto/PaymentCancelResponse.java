package roomescape.payment.dto;

public record PaymentCancelResponse (
        String paymentKey,
        String orderId,
        Long totalAmount
){

}
