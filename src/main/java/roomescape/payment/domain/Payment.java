package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.Objects;
import roomescape.reservation.domain.Reservation;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;
    private String orderId;
    private int amount;
    private LocalDateTime approvedAt;

    @OneToOne
    private Reservation reservation;

    protected Payment() {
    }

    public Payment(final Long id, final String paymentKey, final String orderId, final int amount,
                   final LocalDateTime approvedAt, final Reservation reservation) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.approvedAt = approvedAt;
        this.reservation = reservation;
    }

    public static Payment createWithoutId(final String paymentKey,
                                          final String orderId,
                                          final int amount,
                                          final LocalDateTime approvedAt,
                                          final Reservation reservation
    ) {
        return new Payment(null, paymentKey, orderId, amount, approvedAt, reservation);
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getAmount() {
        return amount;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final Payment payment)) {
            return false;
        }
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
