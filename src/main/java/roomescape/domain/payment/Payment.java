package roomescape.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Entity
public class Payment {

    @Id
    private final String paymentKey;

    private final Long reservationId;

    public Payment(Long reservationId, String paymentKey) {
        this.reservationId = reservationId;
        this.paymentKey = paymentKey;
    }
}
