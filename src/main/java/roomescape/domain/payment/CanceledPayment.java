package roomescape.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
import java.util.Objects;
import roomescape.domain.reservation.CanceledReservation;

@Entity
public class CanceledPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String paymentKey;
    @Column(nullable = false)
    private String orderId;
    @Column(nullable = false)
    private BigDecimal totalAmount;
    @OneToOne(optional = false)
    private CanceledReservation canceledReservation;

    protected CanceledPayment() {
    }

    public CanceledPayment(String paymentKey, String orderId, BigDecimal totalAmount, CanceledReservation canceledReservation) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.canceledReservation = canceledReservation;
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

    public CanceledReservation getCanceledReservation() {
        return canceledReservation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CanceledPayment canceledPayment = (CanceledPayment) o;
        return Objects.equals(id, canceledPayment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
