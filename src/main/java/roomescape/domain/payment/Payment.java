package roomescape.domain.payment;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import roomescape.domain.reservation.Reservation;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Reservation reservation;

    private String paymentKey;

    private String orderId;

    private Long amount;

    protected Payment() {
    }

    public Payment(final Reservation reservation, final String paymentKey, final String orderId, final Long amount) {
        this(null, reservation, paymentKey, orderId, amount);
    }

    public Payment(
            final Long id,
            final Reservation reservation,
            final String paymentKey,
            final String orderId,
            final Long amount
    ) {
        this.id = id;
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
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

    public Long getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof Payment other
                && Objects.equals(getId(), other.getId())
                && Objects.equals(getReservation(), other.getReservation())
                && Objects.equals(getPaymentKey(), other.getPaymentKey())
                && Objects.equals(getOrderId(), other.getOrderId())
                && Objects.equals(getAmount(), other.getAmount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reservation, paymentKey, orderId, amount);
    }
}
