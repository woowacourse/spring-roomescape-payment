package roomescape.payment.dto.response;

public record ConfirmPaymentResponse(String paymentKey,
                                     String orderId,
                                     Long totalAmount,
                                     String status) {

    private static final String SUCCESS_STATUS = "DONE";

    public boolean isCanceled() {
        return !SUCCESS_STATUS.equals(status);
    }
}
