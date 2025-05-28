package roomescape.domain.payment;

public record PaymentDetails(
        PaymentConfirmation paymentConfirmation,
        String code,
        String message
) {

    private static final String SUCCESS_CODE = "success";

    public boolean isFailed() {
        return !SUCCESS_CODE.equals(code);
    }
}
