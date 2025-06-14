package roomescape.presentation.dto.response;

public record PaymentApproveResponseDto(String paymentKey,
                                        String orderId,
                                        Long totalAmount) {
}
