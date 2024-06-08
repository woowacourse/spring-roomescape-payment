package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reservationId;

    private Long memberId;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private Long amount;

    public Payment() {
    }

    public Payment(Long id, Long reservationId, Long memberId, String paymentKey, String orderId, Long amount) {
        this.id = id;
        this.reservationId = reservationId;
        this.memberId = memberId;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }

    public Payment(Long reservationId, Long memberId, String paymentKey, String orderId, Long amount) {
        this(null, reservationId, memberId, paymentKey, orderId, amount);
    }

    public Long getId() {
        return id;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public Long getMemberId() {
        return memberId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id)
                && Objects.equals(reservationId, payment.reservationId)
                && Objects.equals(memberId, payment.memberId)
                && Objects.equals(paymentKey, payment.paymentKey)
                && Objects.equals(orderId, payment.orderId)
                && Objects.equals(amount, payment.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reservationId, memberId, paymentKey, orderId, amount);
    }
}
