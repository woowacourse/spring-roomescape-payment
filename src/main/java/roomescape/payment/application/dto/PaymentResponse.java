package roomescape.payment.application.dto;

public record PaymentResponse(
        String paymentKey,
        Long totalAmount
) {

}
