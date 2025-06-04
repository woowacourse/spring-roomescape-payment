package roomescape.application.payment.dto;

public record PaymentValidationCommand(
        String orderId,
        Long amount
) {

}
