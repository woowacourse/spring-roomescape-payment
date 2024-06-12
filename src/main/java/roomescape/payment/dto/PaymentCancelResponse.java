package roomescape.payment.dto;

public record PaymentCancelResponse(
        String paymentKey,
        String orderId,
        Long totalAmount,
        String status
) {

    public boolean isCancelNotFinished() {
        return !this.status.equals("CANCELED");
    }
}
