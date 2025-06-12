package roomescape.domain.reservation;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.domain.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationPayment extends BaseEntity {

    private Long reservationId;

    private Long paymentId;

    public ReservationPayment(final Long reservationId, final Long PaymentId) {
        this.reservationId = reservationId;
        this.paymentId = PaymentId;
    }
}
