package roomescape.domain.reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.math.BigInteger;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Reservation reservation;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private BigInteger amount;

    public Payment(Reservation reservation, String paymentKey, String orderId, BigInteger amount) {
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }

    protected Payment() {

    }
}
