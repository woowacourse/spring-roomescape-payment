package roomescape.domain.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.domain.payment.exception.PaymentException;
import roomescape.domain.reservation.entity.Reservation;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Column(unique = true, name = "payment_key", nullable = false)
    private String paymentKey;

    @Column(unique = true, name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "amount", nullable = false)
    private int amount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    public Payment(final Long id, final String paymentKey, final String orderId, final int amount,
                   final Reservation reservation) {
        validatePaymentKey(paymentKey);
        validateOrderId(orderId);
        validateAmount(amount);
        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.reservation = reservation;
    }

    public Payment(final String paymentKey, final String orderId, final int amount, final Reservation reservation) {
        validatePaymentKey(paymentKey);
        validateOrderId(orderId);
        validateAmount(amount);
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.reservation = reservation;
    }

    //TODO : 커스텀 예외 추가
    private void validatePaymentKey(final String paymentKey) {
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new PaymentException("결제키는 NULL, 공백이 허용되지 않습니다.");
        }
    }

    private void validateOrderId(final String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new PaymentException("주문ID는 NULL, 공백이 허용되지 않습니다.");
        }
    }

    private void validateAmount(final int amount) {
        if (amount <= 0) {
            throw new PaymentException("구매 금액은 0원보다 작을 수 없습니다.");
        }
    }
}

