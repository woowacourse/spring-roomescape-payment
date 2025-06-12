package roomescape.reservation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import roomescape.payment.domain.Payment;

@Entity
@Getter
public class PaymentReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @OneToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    protected PaymentReservation() {
    }

    public PaymentReservation(Long id, Reservation reservation, Payment payment) {
        this.id = id;
        this.reservation = reservation;
        this.payment = payment;
    }

    public PaymentReservation(Reservation reservation, Payment payment) {
        this(null, reservation, payment);
    }
}
