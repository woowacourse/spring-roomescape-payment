package roomescape.domain.payment;

public record PaymentDetails(
        PaymentConfirmation confirmation,
        PaymentFailure failure
) {

    public PaymentDetails(final PaymentConfirmation confirmation) {
        this(confirmation, null);
    }

    public PaymentDetails(final PaymentFailure failure) {
        this(null, failure);
    }

    public boolean isFailed() {
        return failure != null;
    }
}
