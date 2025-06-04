package roomescape.infrastructure.payment;

public record PaymentApproveResponseDto(String paymentKey,
                                        String orderId,
                                        Long totalAmount) {
}
