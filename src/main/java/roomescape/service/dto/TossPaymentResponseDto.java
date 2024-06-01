package roomescape.service.dto;

public record TossPaymentResponseDto(
    String paymentKey,
    String orderName
) { }
