package roomescape.payment.domain;

public interface PaymentRepository {
    PaymentInfo save(PaymentInfo paymentInfo);

    PaymentInfo findById(Long paymentId);
}
