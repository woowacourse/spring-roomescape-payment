package roomescape.domain.payment;

public record PaymentDetails(
        PaymentConfirmation confirmation,
        PaymentStatus status
) {

    public PaymentDetails(final PaymentConfirmation confirmation) {
        this(confirmation, PaymentStatus.succeed());
    }

    public PaymentDetails(final PaymentStatus status) {
        this(null, status);
    }

    public boolean isFailed() {
        return status.isFailed();
    }
}
