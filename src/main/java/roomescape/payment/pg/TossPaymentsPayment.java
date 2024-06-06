package roomescape.payment.pg;

public record TossPaymentsPayment() {

    public boolean verify(TossPaymentsConfirmRequest request) {
        return false;
    }
}
