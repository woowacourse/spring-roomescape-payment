package roomescape.domain.payment;

import jakarta.persistence.Entity;
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

    @OneToOne
    private Reservation reservation;

    protected Payment() {
    }

    public Payment(
        String paymentKey,
        Long totalAmount,
        String requestedAt,
        String approvedAt,
        Reservation reservation
    ) {
        this.paymentKey = paymentKey;
        this.totalAmount = totalAmount;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.reservation = reservation;
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

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
}
