package roomescape.domain.payment;

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
    @OneToOne
    private Reservation reservation;
    @Column(nullable = false)
    private String paymentKey;
    @Column(nullable = false, columnDefinition = "bigint")
    private BigDecimal totalAmount;

    protected Payment() {
    }

    public Payment(Reservation reservation, String paymentKey, BigDecimal totalAmount) {
        this(null, reservation, paymentKey, totalAmount);
    }

    public Payment(Long id, Reservation reservation, String paymentKey, BigDecimal totalAmount) {
        this.id = id;
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.totalAmount = totalAmount;
    }

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
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
        return Objects.hashCode(id);
    }
}
