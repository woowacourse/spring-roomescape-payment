package roomescape.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import roomescape.exception.RoomescapeErrorCode;
import roomescape.exception.RoomescapeException;

@Entity
@SQLRestriction("deleted_at is NULL")
@SQLDelete(sql = "UPDATE payment SET deleted_at = NOW() WHERE id = ?")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    private String paymentKey;

    private String orderId;

    private BigDecimal amount;

    protected Payment() {
    }

    public Payment(Reservation reservation, String paymentKey, String orderId, BigDecimal amount) {
        this(null, reservation, paymentKey, orderId, amount);
    }

    public Payment(Long id, Reservation reservation, String paymentKey, String orderId, BigDecimal amount) {
        validateReservation(reservation);
        validatePaymentKey(paymentKey);
        validateOrderId(orderId);
        validateAmount(amount);
        this.id = id;
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }

    private void validateReservation(Reservation reservation) {
        if (reservation == null) {
            throw new RoomescapeException(RoomescapeErrorCode.BAD_REQUEST, "결제 예약은 비어있을 수 없습니다.");
        }
    }

    private void validatePaymentKey(String paymentKey) {
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new RoomescapeException(RoomescapeErrorCode.BAD_REQUEST, "결제 키는 비어있을 수 없습니다.");
        }
    }

    private void validateOrderId(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new RoomescapeException(RoomescapeErrorCode.BAD_REQUEST, "결제 주문id는 비어있을 수 없습니다.");
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RoomescapeException(RoomescapeErrorCode.BAD_REQUEST, "결제 금액은 0원 이하일 수 없습니다.");
        }
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

    public BigDecimal getAmount() {
        return amount;
    }
}
