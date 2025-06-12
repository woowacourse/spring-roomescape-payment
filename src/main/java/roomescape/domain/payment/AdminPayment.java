package roomescape.domain.payment;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.domain.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminPayment extends BaseEntity {

    private Long paymentId;

    private Long adminId;

    public AdminPayment(final Long paymentId, final Long adminId) {
        this.paymentId = paymentId;
        this.adminId = adminId;
    }
}
