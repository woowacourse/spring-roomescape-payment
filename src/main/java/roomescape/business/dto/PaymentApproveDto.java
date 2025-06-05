package roomescape.business.dto;

public record PaymentApproveDto(
        String paymentKey,
        String orderId,
        Long amount
) {
    public static PaymentApproveDto of(String paymentKey, String orderId, Long amount) {
        return new PaymentApproveDto(paymentKey, orderId, amount);
    }
}
