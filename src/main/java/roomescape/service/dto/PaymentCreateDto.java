package roomescape.service.dto;

public record PaymentCreateDto(
        String paymentKey,
        String orderId,
        Long totalAmount
) {
}
