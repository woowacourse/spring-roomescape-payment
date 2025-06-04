package roomescape.domain.payment;

import jakarta.persistence.Embedded;
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
import lombok.experimental.Accessors;

@EqualsAndHashCode(of = {"id"})
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "PAYMENT")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Embedded
    private PaymentKey paymentKey;
    @Embedded
    private OrderId orderId;
    private long totalAmount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private long reservationId;

    public Payment(
            final long id,
            final PaymentKey paymentKey,
            final OrderId orderId, final long totalAmount,
            final PaymentStatus status,
            final long reservationId
    ) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.reservationId = reservationId;
    }

    public Payment(final PaymentKey key, final OrderId orderId, final long totalAmount, final PaymentStatus status, final long reservationId) {
        this(0L, key, orderId, totalAmount, status, reservationId);
    }
}
