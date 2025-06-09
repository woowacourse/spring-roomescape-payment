package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentKey;
    private String orderId;
    private OrderStatus status;

    public Orders(String paymentKey, String orderId, OrderStatus status) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.status = status;
        validate();
    }

    public static Orders pending(String paymentKey, String orderId) {
        return new Orders(paymentKey, orderId, OrderStatus.PENDING);
    }

    private void validate() {
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new IllegalArgumentException("Payment key cannot be null or blank");
        }

        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID cannot be null or blank");
        }
    }

    public void success() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("주문 상태를 성공으로 표시하려면 보류 중이어야 합니다.");
        }
        this.status = OrderStatus.SUCCESS;
    }

    public void failed() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("주문 상태를 실패로 표시하려면 보류 중이어야 합니다.");
        }
        this.status = OrderStatus.FAILED;
    }
}
