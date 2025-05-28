package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import roomescape.reservation.domain.Reservation;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;

    private String paymentKey;

    private Long amount;

    @ManyToOne
    private Reservation reservation;

    public Payment(final Long id, final String orderId, final String paymentKey, final Long amount,
                   final Reservation reservation) {
        this.id = id;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.reservation = reservation;
    }

    public Payment(final String orderId, final String paymentKey, final Long amount, final Reservation reservation) {
        this(null, orderId, paymentKey, amount, reservation);
    }
}
