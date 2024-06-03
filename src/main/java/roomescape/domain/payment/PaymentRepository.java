package roomescape.domain.payment;

public interface PaymentRepository {

    Payment save(Payment payment);

    Payment getByOrderId(String orderId);
}
