package roomescape.payment.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private Long totalAmount;

    @Column(nullable = false)
    private Long reservationId;

    public Payment(final String orderId, final String paymentKey, final Long totalAmount, final Long reservationId) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.totalAmount = totalAmount;
        this.reservationId = reservationId;
    }

    public static Payment of(
            final String orderId,
            final String paymentKey,
            final Long totalAmount,
            final Long reservationId
    ) {
        return new Payment(orderId, paymentKey, totalAmount, reservationId);
    }

}
