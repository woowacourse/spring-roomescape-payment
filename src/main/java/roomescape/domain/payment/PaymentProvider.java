package roomescape.domain.payment;

public interface PaymentProvider {

    PaymentDetails confirm(PaymentRequest paymentRequest);
}
