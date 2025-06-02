package roomescape.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.domain.reservation.Reservation;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Entity
public class Payment {

    @Id
    private final String paymentKey;

    @Column(nullable = false)
    private final int amount;

    @OneToOne(optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private final Reservation reservation;

    public Payment(String paymentKey, int amount, Reservation reservation) {
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.reservation = reservation;
    }
}
