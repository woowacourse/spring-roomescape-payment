package roomescape.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Column(nullable = false, precision = 10)
    private BigDecimal totalAmount;
    @Enumerated(value = EnumType.STRING)
    private PaymentStatus status;

    protected Payment() {
    }

    public Payment(Reservation reservation, String paymentKey, BigDecimal totalAmount, PaymentStatus status) {
        this(null, reservation, paymentKey, totalAmount, status);
    }

    public Payment(Reservation reservation, String paymentKey, BigDecimal totalAmount) {
        this(null, reservation, paymentKey, totalAmount, PaymentStatus.COMPLETE);
    }

    public Payment(Long id, Reservation reservation, String paymentKey, BigDecimal totalAmount, PaymentStatus status) {
        this.id = id;
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public void refund() {
        if (status.isCompleted()) {
            status = PaymentStatus.REFUNDED;
        }
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

    public PaymentStatus getStatus() {
        return status;
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
