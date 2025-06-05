package roomescape.presentation.dto.request;

public record PaymentApproveRequestDto(
        String paymentKey,
        String orderId,
        Long amount
) {
    public static PaymentApproveRequestDto of(String paymentKey, String orderId, Long amount) {
        return new PaymentApproveRequestDto(paymentKey, orderId, amount);
    }
}
