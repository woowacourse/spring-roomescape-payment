package roomescape.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "amount", nullable = false)
    private long amount;
    @Column(name = "payment_key", nullable = false)
    private String paymentKey;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", referencedColumnName = "id", nullable = false)
    private Reservation reservation;

    protected Payment() {
    }

    public Payment(long amount, String paymentKey, Reservation reservation) {
        this.amount = amount;
        this.paymentKey = paymentKey;
        this.reservation = reservation;
    }
}
