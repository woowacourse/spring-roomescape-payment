package roomescape.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.util.Objects;
import roomescape.exception.ArgumentNullException;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Reservation reservation;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private int amount;

    public Payment(final Long id, final Reservation reservation, final String orderId, final String paymentKey,
                   final int amount) {
        validateNotNull(reservation, orderId, paymentKey);
        this.id = id;
        this.reservation = reservation;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
    }

    protected Payment() {
    }

    public static Payment createPaymentWithoutId(final String orderId,
                                                 final Reservation reservation,
                                                 final String paymentKey,
                                                 final int amount) {
        return new Payment(null, reservation, orderId, paymentKey, amount);
    }

    private void validateNotNull(Reservation reservation, String orderId, String paymentKey) {
        if (reservation == null) {
            throw new ArgumentNullException("reservation");
        }
        if (orderId == null) {
            throw new ArgumentNullException("orderId");
        }
        if (paymentKey == null) {
            throw new ArgumentNullException("paymentKey");
        }
    }

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
