package roomescape.domain.payment;

public record PaymentFailure(PaymentFailCode code, String message) {

    public boolean causedBy(final PaymentFailCode.Cause cause) {
        return code.causedBy(cause);
    }
}
