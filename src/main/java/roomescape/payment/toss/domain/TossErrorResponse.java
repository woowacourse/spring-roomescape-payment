package roomescape.payment.toss.domain;

public record TossErrorResponse(
    String code,
    String message
) {
} 
