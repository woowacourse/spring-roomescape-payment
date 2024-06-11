package roomescape.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "reservation_payment")
public class ReservationPayment {

    private static final int MIN_AMOUNT = 0;
    public static final int MIN_RESERVATION_ID = 1;

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
        validateReservationId(reservationId);
        validateAmount(amount);
        this.orderId = orderId;
        this.reservationId = reservationId;
        this.paymentKey = paymentKey;
        this.amount = amount;
    }

    private void validateReservationId(Long reservationId) {
        if (reservationId == null || reservationId < MIN_RESERVATION_ID) {
            throw new IllegalArgumentException("결제 정보는 예약 정보가 필수입니다.");
        }
    }

    private void validateAmount(long amount) {
        if (amount < MIN_AMOUNT) {
            throw new IllegalArgumentException("결제 금액은 음수일 수 없습니다.");
        }
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
