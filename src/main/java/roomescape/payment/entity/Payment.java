package roomescape.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_key")
    private String paymentKey;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "payment_type")
    private String paymentType;

    public Payment(String paymentKey, String orderId, Long amount, String paymentType) {
        this(null, paymentKey, orderId, amount, paymentType);
    }
}
