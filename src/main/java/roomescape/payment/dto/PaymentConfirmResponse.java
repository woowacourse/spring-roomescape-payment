package roomescape.payment.dto;

public record PaymentConfirmResponse(
        String paymentKey,
        String orderId,
        Long totalAmount,
        String status
) {

    public boolean isConfirmNotFinished() {
        return !this.status.equals("DONE");
    }
}
