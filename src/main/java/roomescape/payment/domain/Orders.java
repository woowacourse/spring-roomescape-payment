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

    public Orders(String paymentKey, String orderId) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        validate();
    }

    private void validate() {
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new IllegalArgumentException("Payment key cannot be null or blank");
        }

        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID cannot be null or blank");
        }
    }
}
