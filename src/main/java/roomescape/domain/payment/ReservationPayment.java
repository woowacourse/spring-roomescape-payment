package roomescape.domain.payment;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
import roomescape.domain.reservation.Reservation;

@Entity
public class ReservationPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private PaymentInfo info;

    @ManyToOne(optional = false)
    private Reservation reservation;

    protected ReservationPayment() {
    }

    public ReservationPayment(Long id, PaymentInfo info, Reservation reservation) {
        this.id = id;
        this.info = info;
        this.reservation = reservation;
    }

    public ReservationPayment(PaymentInfo info, Reservation reservation) {
        this(null, info, reservation);
    }

    public Long getId() {
        return id;
    }

    public PaymentInfo getInfo() {
        return info;
    }

    public Reservation getReservation() {
        return reservation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReservationPayment reservationPayment = (ReservationPayment) o;
        return Objects.equals(id, reservationPayment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
