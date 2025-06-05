package roomescape.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Entity
public class Payment {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "reservation_id")
    private final Long reservationId;

    private final String paymentKey;

    private final int amount;

    public Payment(Long reservationId, String paymentKey, int amount) {
        this.reservationId = reservationId;
        this.paymentKey = paymentKey;
        this.amount = amount;
    }
}
