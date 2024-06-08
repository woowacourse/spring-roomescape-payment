package roomescape.payment.domain;

public record PaymentCancelResult(String status) {
    private static final String CANCEL_STATUS = "CANCELED";

    public boolean isCorrectStatus() {
        return CANCEL_STATUS.equals(status);
    }
}
