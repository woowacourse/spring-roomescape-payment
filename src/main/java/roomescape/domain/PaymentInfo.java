package roomescape.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "payment")
public class PaymentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;
    @Column(nullable = false)
    private final String orderId;
    @Column(nullable = false)
    private final String paymentKey;
    @Column(nullable = false)
    private final String orderName;
    @Column(nullable = false)
    private final Long totalAmount;
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private final Reservation reservation;

    protected PaymentInfo() {
        this.id = null;
        this.paymentKey = null;
        this.orderId = null;
        this.orderName = null;
        this.totalAmount = null;
        this.reservation = null;
    }

    public PaymentInfo(final Long id, final String orderId, final String paymentKey, final String orderName,
                       final Long totalAmount, final Reservation reservation) {
        this.id = id;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.orderName = orderName;
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

    public String getOrderName() {
        return orderName;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public Reservation getReservation() {
        return reservation;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PaymentInfo payment = (PaymentInfo) o;
        return Objects.equals(getId(), payment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Payment{" +
               "id=" + id +
               ", orderId='" + orderId + '\'' +
               ", paymentKey='" + paymentKey + '\'' +
               ", orderName='" + orderName + '\'' +
               ", totalAmount=" + totalAmount +
               ", reservation=" + reservation +
               '}';
    }
}
