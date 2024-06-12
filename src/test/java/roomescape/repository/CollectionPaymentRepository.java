package roomescape.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import roomescape.domain.Reservation;
import roomescape.domain.payment.Payment;

public class CollectionPaymentRepository implements PaymentRepository {

    private final List<Payment> payments;
    private final AtomicLong atomicLong;

    public CollectionPaymentRepository() {
        this.payments = new ArrayList<>();
        this.atomicLong = new AtomicLong(0);
    }

    @Override
    public Payment save(Payment payment) {
        Payment saved = new Payment(atomicLong.incrementAndGet(), payment);
        payments.add(saved);
        return saved;
    }

    @Override
    public List<Payment> findAllByReservationIn(List<Reservation> reservations) {
        return payments.stream()
                .filter(payment -> reservations.contains(payment.getReservation()))
                .toList();
    }

    @Override
    public Optional<Payment> findByOrderIdAndPaymentKey(String orderId, String paymentKey) {
        return payments.stream()
                .filter(payment -> payment.getOrderId().equals(orderId))
                .filter(payment -> payment.getPaymentKey().equals(paymentKey))
                .findFirst();
    }

    @Override
    public Optional<Payment> findByReservationId(Long reservationId) {
        return payments.stream()
                .filter(payment -> payment.getReservation() != null)
                .filter(payment -> reservationId.equals(payment.getReservation().getId()))
                .findFirst();
    }

    @Override
    public void deleteByReservationId(Long reservationId) {
        payments.removeIf(payment -> reservationId.equals(payment.getReservation().getId()));
    }
}
