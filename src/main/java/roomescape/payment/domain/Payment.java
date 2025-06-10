package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer amount;
    private String orderId;
    private String paymentKey;

    public Payment(final Long id,
                   final Integer amount,
                   final String orderId,
                   final String paymentKey
    ) {
        validateAmount(amount);
        validateOrderId(orderId);
        validatePaymentKey(paymentKey);
        this.id = id;
        this.amount = amount;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
    }

    public Payment(
            final Integer amount,
            final String orderId,
            final String paymentKey
    ) {
        validateAmount(amount);
        validateOrderId(orderId);
        validatePaymentKey(paymentKey);
        this.amount = amount;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
    }

    private void validateAmount(final Integer amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be a positive number");
        }
    }

    private void validateOrderId(final String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID cannot be null or blank");
        }
    }

    private void validatePaymentKey(final String paymentKey) {
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new IllegalArgumentException("Payment key cannot be null or blank");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Payment payment)) {
            return false;
        }
        return Objects.equals(id, payment.id) && Objects.equals(amount, payment.amount)
                && Objects.equals(orderId, payment.orderId) && Objects.equals(paymentKey,
                payment.paymentKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, orderId, paymentKey);
    }
}
