package roomescape.application.payment.dto;

public record CreatePaymentCommand(
        String orderId,
        Long amount
) {
}
