package roomescape.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
import roomescape.domain.reservation.Reservation;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;

    private BigDecimal amount;

    @OneToOne
    private Reservation reservation;

    protected Payment() {
    }

    public Payment(final String paymentKey, final BigDecimal amount, final Reservation reservation) {
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.reservation = reservation;
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
