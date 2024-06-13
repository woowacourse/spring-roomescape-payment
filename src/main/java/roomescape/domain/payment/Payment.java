package roomescape.domain.payment;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
import java.util.Objects;
import roomescape.domain.reservation.Reservation;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String paymentKey;
    @Column(nullable = false)
    private String orderId;
    @Column(nullable = false)
    private BigDecimal totalAmount;
    @OneToOne(optional = false, cascade = CascadeType.REMOVE)
    private Reservation reservation;

    protected Payment() {
    }

    public Payment(String paymentKey, String orderId, BigDecimal totalAmount, Reservation reservation) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Reservation getReservation() {
        return reservation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
