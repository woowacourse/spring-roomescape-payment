package roomescape.application.payment.dto;

public record TossPaymentValidationCommand(
        String orderId,
        Long amount
) {

}
