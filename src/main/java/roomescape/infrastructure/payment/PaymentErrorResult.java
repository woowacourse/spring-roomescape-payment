package roomescape.infrastructure.payment;

public record PaymentErrorResult(String code, String message, String data) {
}
