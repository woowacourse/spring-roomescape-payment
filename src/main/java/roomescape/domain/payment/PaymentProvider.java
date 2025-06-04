package roomescape.domain.payment;

public interface PaymentProvider {

    PaymentExecutionResult confirm(PaymentRequest paymentRequest);
}
