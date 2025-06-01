package roomescape.payment.processor;

public interface PaymentProcessor {
    boolean supports(final PaymentType paymentType);

    PaymentConfirmResponse processPayment(final PaymentConfirmRequest request);
}
