package roomescape.domain.payment;

public record PaymentExecutionResult(
        PaymentConfirmation confirmation,
        TransactionStatus status
) {

    public PaymentExecutionResult(final PaymentConfirmation confirmation) {
        this(confirmation, TransactionStatus.succeed());
    }

    public PaymentExecutionResult(final TransactionStatus status) {
        this(null, status);
    }

    public boolean isFailed() {
        return status.isFailed();
    }
}
