package roomescape.payment.tosspayment.domain;

public record TossPaymentErrorResponse(
    String code,
    String message
) {
} 
