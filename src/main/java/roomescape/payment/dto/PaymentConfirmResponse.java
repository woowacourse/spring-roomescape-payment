package roomescape.payment.dto;

public record PaymentConfirmResponse(
        String paymentKey,
        String orderId,
        Long totalAmount,
        String status
) {

    public boolean isPaymentNotFinished() {
        return !this.status.equals("DONE");
    }
}
