package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.reservation.domain.Reservation;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;

    private String paymentKey;

    private BigDecimal amount;

    @ManyToOne
    private Reservation reservation;

    @Enumerated(value = EnumType.STRING)
    private PaymentStatus status;

    private Payment(final Long id, final String orderId, final String paymentKey, final BigDecimal amount,
                    final Reservation reservation, final PaymentStatus status) {
        this.id = id;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.reservation = reservation;
        this.status = status;
    }

    public static Payment pending(
            final String orderId,
            final String paymentKey,
            final BigDecimal amount,
            final Reservation reservation
    ) {
        return new Payment(null, orderId, paymentKey, amount, reservation, PaymentStatus.PENDING);
    }

    public static Payment await(
            final String orderId,
            final BigDecimal amount,
            final Reservation reservation
    ) {
        return new Payment(null, orderId, null, amount, reservation, PaymentStatus.AWAIT);
    }

    public void success() {
        this.status = PaymentStatus.SUCCESS;
    }

    public void fail() {
        this.status = PaymentStatus.FAILED;
    }
}
