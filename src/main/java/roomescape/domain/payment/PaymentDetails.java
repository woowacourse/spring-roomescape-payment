package roomescape.domain.payment;

public record PaymentDetails(
        PaymentConfirmation confirmation,
        TransactionStatus status
) {

    public PaymentDetails(final PaymentConfirmation confirmation) {
        this(confirmation, TransactionStatus.succeed());
    }

    public PaymentDetails(final TransactionStatus status) {
        this(null, status);
    }

    public boolean isFailed() {
        return status.isFailed();
    }
}
