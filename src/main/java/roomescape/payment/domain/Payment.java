package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = {"id"})
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private Long amount;

    private Payment(
            final String paymentKey,
            final String orderId,
            final Long amount
    ) {
        validatePaymentKey(paymentKey);
        validateOrderId(orderId);
        validateAmount(amount);

        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }

    public static Payment ofOfflinePayment() {
        return new Payment("offline-payment-key", "offline-order-id", 0L);
    }

    public static Payment of(
            final String paymentKey,
            final String orderId,
            final Long amount
    ) {
        return new Payment(paymentKey, orderId, amount);
    }

    private void validatePaymentKey(final String paymentKey) {
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new IllegalArgumentException("paymentKey는 null이거나 공백일 수 없습니다.");
        }
    }

    private void validateOrderId(final String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId는 null이거나 공백일 수 없습니다.");
        }
    }

    private void validateAmount(final Long amount) {
        if (amount == null) {
            throw new IllegalArgumentException("주문 금액은 null일 수 없습니다.");
        }

        if (amount < 0) {
            throw new IllegalArgumentException("주문 금액은 음수일 수 없습니다.");
        }
    }
}
