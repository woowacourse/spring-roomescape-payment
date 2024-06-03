package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Objects;
import roomescape.reservation.domain.Reservation;

@Entity
@Table(name = "payment_history", uniqueConstraints = @UniqueConstraint(columnNames = "reservation_id"))
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @OneToOne(optional = false)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;
    @Column(name = "payment_key")
    private String paymentKey;

    public Payment(Reservation reservation, String paymentKey) {
        this.reservation = reservation;
        this.paymentKey = paymentKey;
    }

    protected Payment() {
    }

    public Long getId() {
        return id;
    }

    public Long getReservationId() {
        return reservation.getId();
    }

    public String getPaymentKey() {
        return paymentKey;
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
        Payment that = (Payment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Payment{" +
               "id=" + id +
               ", reservation=" + reservation +
               ", paymentKey='" + paymentKey + '\'' +
               '}';
    }
}
