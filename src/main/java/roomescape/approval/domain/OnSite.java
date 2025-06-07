package roomescape.approval.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.reservation.domain.Reservation;

@Entity
@Getter
@DiscriminatorValue("ONSITE")
@NoArgsConstructor
public class OnSite extends Approval {
    private BigDecimal amount;

    public OnSite(Reservation reservation, BigDecimal amount) {
        super(reservation);
        this.amount = amount;
    }

    @Override
    public ApprovalType getType() {
        return ApprovalType.ONSITE;
    }
}
