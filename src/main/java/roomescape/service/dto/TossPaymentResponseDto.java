package roomescape.service.dto;

public record TossPaymentResponseDto(
    String paymentKey,
    long totalAmount,
    String method
) { }
