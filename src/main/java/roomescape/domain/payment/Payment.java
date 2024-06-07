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
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private PaymentInfo info;

    @ManyToOne(optional = false)
    private Reservation reservation;

    protected Payment() {
    }

    public Payment(Long id, PaymentInfo info, Reservation reservation) {
        this.id = id;
        this.info = info;
        this.reservation = reservation;
    }

    public Payment(PaymentInfo info, Reservation reservation) {
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
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
