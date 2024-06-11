package roomescape.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class PaymentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentKey;
    private String paymentId;
    private Long amount;
    @OneToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    protected PaymentInfo() {
    }

    public PaymentInfo(String paymentKey, String paymentId, Long amount, Reservation reservation) {
        this(null, paymentKey, paymentId, amount, reservation);
    }

    public PaymentInfo(Long id, String paymentKey, String paymentId, Long amount, Reservation reservation) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.paymentId = paymentId;
        this.amount = amount;
        this.reservation = reservation;
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getPaymentId() {
        return paymentId;
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
        return Objects.equals(id, that.id) && Objects.equals(paymentKey, that.paymentKey) && Objects.equals(paymentId, that.paymentId) && Objects.equals(amount, that.amount) && Objects.equals(reservation, that.reservation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, paymentKey, paymentId, amount, reservation);
    }

    @Override
    public String toString() {
        return "PaymentInfo{" +
                "id=" + id +
                ", paymentKey='" + paymentKey + '\'' +
                ", paymentId='" + paymentId + '\'' +
                ", amount=" + amount +
                ", reservation=" + reservation +
                '}';
    }
}
