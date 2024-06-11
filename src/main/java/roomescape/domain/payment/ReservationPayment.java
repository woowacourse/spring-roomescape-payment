package roomescape.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "reservation_payment")
public class ReservationPayment {
    private static final ReservationPayment EMPTY_INSTANCE = new ReservationPayment(null, null, null, 0);

    @Id
    private String orderId;

    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @Column(name = "payment_key", nullable = false)
    private String paymentKey;

    @Column(name = "amount", nullable = false)
    private long amount;

    protected ReservationPayment() {
    }

    public ReservationPayment(String orderId, Long reservationId, String paymentKey, long amount) {
        this.orderId = orderId;
        this.reservationId = reservationId;
        this.paymentKey = paymentKey;
        this.amount = amount;
    }

    public static ReservationPayment getEmptyInstance() {
        return EMPTY_INSTANCE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReservationPayment other)) {
            return false;
        }
        return Objects.equals(orderId, other.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public long getAmount() {
        return amount;
    }
}
