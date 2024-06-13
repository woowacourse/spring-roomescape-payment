package roomescape.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import roomescape.domain.reservation.Reservation;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;

    private Long totalAmount;

    private String requestedAt;

    private String approvedAt;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @OneToOne
    private Reservation reservation;

    protected Payment() {
    }

    public Payment(
        String paymentKey,
        Long totalAmount,
        String requestedAt,
        String approvedAt,
        PaymentStatus status
    ) {
        this.paymentKey = paymentKey;
        this.totalAmount = totalAmount;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.status = status;
    }

    public void complete(Reservation reservation) {
        this.reservation = reservation;
    }

    public void cancel() {
        this.status = PaymentStatus.CANCELED;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public Long getId() {
        return id;
    }
}
