package roomescape.reservation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class ReservationPayment {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    protected ReservationPayment() {
    }

    public ReservationPayment(Reservation reservation, Payment payment) {
        validateReservation(reservation);
        this.reservation = reservation;
        validatePayment(payment);
        this.payment = payment;
    }

    private void validateReservation(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("예약 정보를 입력해주세요.");
        }
    }

    private void validatePayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("결제 정보를 입력해주세요.");
        }
    }

    public Reservation getReservation() {
        return reservation;
    }

    public Payment getPayment() {
        return payment;
    }
}
