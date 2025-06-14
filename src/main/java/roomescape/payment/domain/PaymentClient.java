package roomescape.payment.domain;

public interface PaymentClient {

    void approvePayment(Payment payment);
}
