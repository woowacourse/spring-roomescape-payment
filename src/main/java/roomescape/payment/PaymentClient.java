package roomescape.payment;

public interface PaymentClient {

    PaymentResponse confirmPayment(PaymentRequest paymentRequest);

    PaymentCancelResponse cancelPayment(PaymentCancelRequest cancelRequest);
}
