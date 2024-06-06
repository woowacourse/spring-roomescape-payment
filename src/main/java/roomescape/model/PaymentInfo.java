package roomescape.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class PaymentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentKey;
    private String orderId;
    private Long amount;
    @OneToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    protected PaymentInfo() {
    }

    public PaymentInfo(String paymentKey, String orderId, Long amount, Reservation reservation) {
        this(null, paymentKey, orderId, amount, reservation);
    }

    public PaymentInfo(Long id, String paymentKey, String orderId, Long amount, Reservation reservation) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
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

    public Long getAmount() {
        return amount;
    }

    public Reservation getReservation() {
        return reservation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentInfo that = (PaymentInfo) o;
        return Objects.equals(id, that.id) && Objects.equals(paymentKey, that.paymentKey) && Objects.equals(orderId, that.orderId) && Objects.equals(amount, that.amount) && Objects.equals(reservation, that.reservation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, paymentKey, orderId, amount, reservation);
    }

    @Override
    public String toString() {
        return "PaymentInfo{" +
                "id=" + id +
                ", paymentKey='" + paymentKey + '\'' +
                ", orderId='" + orderId + '\'' +
                ", amount=" + amount +
                ", reservation=" + reservation +
                '}';
    }
}
