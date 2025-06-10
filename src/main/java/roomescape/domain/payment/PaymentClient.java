package roomescape.domain.payment;

public interface PaymentClient {

    Payment requestPay(PaymentRequest paymentRequest);
}
