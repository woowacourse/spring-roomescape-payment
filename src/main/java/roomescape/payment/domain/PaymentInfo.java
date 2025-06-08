package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import roomescape.reservation.domain.Reservation;

@Getter
@Entity
public class PaymentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;

    private String orderId;

    private long amount;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = true, unique = true)
    private Reservation reservation;

    protected PaymentInfo() {
    }

    public PaymentInfo(String paymentKey, String orderId, long amount, Reservation reservation) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.reservation = reservation;
    }

    public void disconnectReservation() {
        this.reservation = null;
    }
}
