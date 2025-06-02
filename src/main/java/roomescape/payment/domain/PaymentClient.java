package roomescape.payment.domain;

public interface PaymentClient {

    void approvePayment(PaymentInfo paymentInfo);
}
