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
    @Column(nullable = false)
    private Long memberId;
    private String paymentKey;
    @Column(nullable = false)
    private BigDecimal amount;

    public Payment(Long reservationId, Long memberId, String paymentKey, BigDecimal amount) {
        this(null, reservationId, memberId, paymentKey, amount);
    }

    public Payment(Long id, Long reservationId, Long memberId, String paymentKey, BigDecimal amount) {
        this.id = id;
        this.reservationId = reservationId;
        this.memberId = memberId;
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

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}
