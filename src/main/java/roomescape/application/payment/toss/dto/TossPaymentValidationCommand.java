package roomescape.application.payment.toss.dto;

public record TossPaymentValidationCommand(
        String orderId,
        long amount
) {

}
