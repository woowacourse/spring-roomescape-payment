package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import roomescape.reservation.domain.Reservation;

@Entity
@Table(name = "payment", uniqueConstraints = @UniqueConstraint(columnNames = "reservation_id"))
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @OneToOne(optional = false)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;
    @Column(name = "payment_key", nullable = false)
    private String paymentKey;
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;
    @Column(name = "order_id", nullable = false)
    private String orderId;
    @Column(name = "approved_at", nullable = false)
    private LocalDateTime approvedAt;

    public Payment(Reservation reservation, String paymentKey, BigDecimal totalAmount, String orderId, LocalDateTime approvedAt) {
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.totalAmount = totalAmount;
        this.orderId = orderId;
        this.approvedAt = approvedAt;
    }

    protected Payment() {
    }

    public Long getId() {
        return id;
    }

    public Long getReservationId() {
        return reservation.getId();
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Payment that = (Payment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Payment{" +
               "id=" + id +
               ", reservation=" + reservation +
               ", paymentKey='" + paymentKey + '\'' +
               ", totalAmount=" + totalAmount +
               ", orderId='" + orderId + '\'' +
               ", approvedAt=" + approvedAt +
               '}';
    }
}
