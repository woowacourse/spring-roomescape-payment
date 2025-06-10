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

    private long reservationId;

    private String orderId;

    private String paymentKey;

    private long amount;

    @Enumerated(EnumType.STRING)
    private PaymentType type;

    public Payment(long reservationId, String orderId, String paymentKey, long amount, PaymentType type) {
        this.reservationId = reservationId;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.type = type;
    }

    public static Payment empty() {
        return new Payment(0, "", "", 0, PaymentType.NONE);
    }
}
