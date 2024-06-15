package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
import java.util.Objects;
import roomescape.reservation.domain.Reservation;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(nullable = false)
    private Reservation reservation;

    @Column(nullable = false)
    private String paymentKey;

    @Embedded
    @Column(nullable = false)
    private Amount amount;

    public Payment(Long id, Reservation reservation, String paymentKey, BigDecimal amount) {
        this.id = id;
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.amount = new Amount(amount);
    }

    public Payment(Reservation reservation, String paymentKey, BigDecimal amount) {
        this(null, reservation, paymentKey, amount);
    }

    protected Payment() {
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

    public BigDecimal getAmount() {
        return amount.getValue();
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
        return Objects.hash(id, reservation, paymentKey, amount);
    }
}
