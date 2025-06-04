package roomescape.payment.controller.dto;

public record PaymentVerificationWebRequest(
        String orderId,
        int amount
) {
}
