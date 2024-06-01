package roomescape.domain.payment;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import roomescape.domain.reservation.Reservation;

@Entity
@Table(name = "reservation_payment")
public class ReservationPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(name = "payment_key", nullable = false)
    private String paymentKey;

    @Column(name = "amount", nullable = false)
    private long amount;

    protected ReservationPayment() {
    }

    public ReservationPayment(String orderId, Reservation reservation, String paymentKey, long amount) {
        this.orderId = orderId;
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReservationPayment other)) {
            return false;
        }
        return Objects.equals(orderId, other.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    public Long getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public long getAmount() {
        return amount;
    }
}
