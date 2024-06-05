package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long reservationId;
    private String paymentKey;
    @Column(nullable = false)
    private BigDecimal amount;

    public Payment(Long reservationId, String paymentKey, BigDecimal amount) {
        this(null, reservationId, paymentKey, amount);
    }

    public Payment(Long id, Long reservationId, String paymentKey, BigDecimal amount) {
        this.id = id;
        this.reservationId = reservationId;
        this.paymentKey = paymentKey;
        this.amount = amount;
    }

    protected Payment() {
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

    public Long getReservationId() {
        return reservationId;
    }
}
