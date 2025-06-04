package roomescape.domain.payment;

public interface PaymentProvider {

    Payment confirm(PaymentRequest paymentRequest);
}
