package roomescape.approval.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.reservation.domain.Reservation;

@Entity
@Getter
@DiscriminatorValue("PAYMENT")
@NoArgsConstructor
public class Payment extends Approval {
    private String orderId;
    private String paymentKey;
    private BigDecimal amount;

    public Payment(Reservation reservation, String orderId, String paymentKey, BigDecimal amount) {
        super(reservation);
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
    }
}
