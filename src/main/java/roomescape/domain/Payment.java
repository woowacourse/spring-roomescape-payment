package roomescape.domain;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;
    @Column(nullable = false)
    private final String paymentKey;
    @Column(nullable = false)
    private final String orderId;
    @Column(nullable = false)
    private final Long amount;
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private final Reservation reservation;

    protected Payment() {
        this.id = null;
        this.paymentKey = null;
        this.orderId = null;
        this.amount = null;
        this.reservation = null;
    }

    public Payment(final Long id, final String paymentKey, final String orderId, final Long amount, final Reservation reservation) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.reservation = reservation;
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getAmount() {
        return amount;
    }

    public Reservation getReservation() {
        return reservation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", paymentKey='" + paymentKey + '\'' +
                ", orderId='" + orderId + '\'' +
                ", amount=" + amount +
                ", reservation=" + reservation +
                '}';
    }
}
