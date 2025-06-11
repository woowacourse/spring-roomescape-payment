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
    private Long amount;

    @Column(nullable = false)
    private Long reservationId;

    private Payment(final String orderId, final String paymentKey, final Long amount, final Long reservationId) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.reservationId = reservationId;
    }

    public static Payment of(
            final String orderId,
            final String paymentKey,
            final Long amount,
            final Long reservationId
    ) {
        return new Payment(orderId, paymentKey, amount, reservationId);
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getAmount() {
        return amount;
    }

    public Long getReservationId() {
        return reservationId;
    }

}
