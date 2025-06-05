package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long reservation_id;

    private String orderId;

    private String paymentKey;

    private long amount;

    @Enumerated(EnumType.STRING)
    private PaymentType type;

    public Payment(long reservation_id, String orderId, String paymentKey, long amount, PaymentType type) {
        this.reservation_id = reservation_id;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.type = type;
    }
}
