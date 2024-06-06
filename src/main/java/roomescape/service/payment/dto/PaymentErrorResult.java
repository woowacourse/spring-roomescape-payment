package roomescape.service.payment.dto;

public record PaymentErrorResult(
        String code,
        String message,
        String data
) {
}
