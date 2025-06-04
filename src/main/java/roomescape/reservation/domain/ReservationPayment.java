package roomescape.reservation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.payment.domain.Payment;

@Entity
@Getter
@NoArgsConstructor
public class ReservationPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Payment payment;

    @ManyToOne
    private Reservation reservation;

    public ReservationPayment(final Payment payment, final Reservation reservation) {
        this.payment = payment;
        this.reservation = reservation;
    }
}
