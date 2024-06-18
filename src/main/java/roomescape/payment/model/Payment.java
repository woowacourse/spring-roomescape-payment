package roomescape.payment.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import roomescape.common.model.BaseEntity;
import roomescape.reservation.model.Reservation;

import java.util.Objects;

@Entity
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    private String paymentKey;

    private String orderId;

    private Long totalAmount;

    public Payment(final Reservation reservation, final String paymentKey, final String orderId, final Long totalAmount) {
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
    }

    protected Payment() {
    }

    public void assignReservation(final Reservation reservation) {
        this.reservation = reservation;
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

    public String getOrderId() {
        return orderId;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Payment payment = (Payment) object;
        return Objects.equals(id, payment.id);
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
               ", orderId='" + orderId + '\'' +
               ", totalAmount=" + totalAmount +
               '}';
    }
}
