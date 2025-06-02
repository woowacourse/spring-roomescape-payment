package roomescape.domain.payment;

public interface PaymentProvider {

    PaymentConfirmation confirm(PaymentRequest paymentRequest);
}
