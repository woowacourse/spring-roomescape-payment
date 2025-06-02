package roomescape.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    private final int amount;
    
    @OneToOne
    private final Reservation reservation;

    public Payment(String paymentKey, int amount, Reservation reservation) {
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.reservation = reservation;
    }
}
