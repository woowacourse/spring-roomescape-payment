package roomescape.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import roomescape.exception.BusinessRuleViolationException;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String orderName;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private Payment(
            final Long id,
            final String paymentKey,
            final String orderId,
            final String orderName,
            final Long amount,
            final PaymentStatus status
    ) {

        validatePaymentKey(paymentKey);
        validateOrderId(orderId);
        validateOrderName(orderName);
        validateAmount(amount);
        validateStatus(status);

        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.orderName = orderName;
        this.amount = amount;
        this.status = status;
    }

    public static Payment register(
            final String paymentKey, final String orderId, final String orderName,
            final Long amount
    ) {
        return new Payment(null, paymentKey, orderId, orderName, amount, PaymentStatus.PENDING);
    }

    public void completePayment() {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("결제 대기 상태에서만 성공 상태로 변경할 수 있습니다.");
        }

        this.status = PaymentStatus.SUCCESS;
    }

    public void rejectPayment() {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("결제 대기 상태에서만 실패 상태로 변경할 수 있습니다.");
        }

        this.status = PaymentStatus.FAILED;
    }

    private void validatePaymentKey(final String paymentKey) {
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new BusinessRuleViolationException("결제 요청 번호는 null이거나 공백일 수 없습니다.");
        }
    }

    private void validateOrderId(final String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new BusinessRuleViolationException("주문 번호는 null이거나 공백일 수 없습니다.");
        }
    }

    private void validateOrderName(final String orderName) {
        if (orderName == null || orderName.isBlank()) {
            throw new BusinessRuleViolationException("주문 이름은 null이거나 공백일 수 없습니다.");
        }
    }

    private void validateAmount(final Long amount) {
        if (amount == null) {
            throw new BusinessRuleViolationException("결제 금액은 null일 수 없습니다.");
        }
        if (amount < 0) {
            throw new BusinessRuleViolationException("결제 금액은 음수일 수 없습니다.");
        }
    }

    private void validateStatus(final PaymentStatus status) {
        if (status == null) {
            throw new BusinessRuleViolationException("결제 상태는 null일 수 없습니다.");
        }
    }
}
