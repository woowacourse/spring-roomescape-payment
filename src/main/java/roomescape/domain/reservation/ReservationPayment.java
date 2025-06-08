package roomescape.domain.reservation;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.domain.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationPayment extends BaseEntity {

    private Long reservationId;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private Long PaymentId;

    public ReservationPayment(final Long reservationId, final PaymentType paymentType, final Long PaymentId) {
        this.reservationId = reservationId;
        this.paymentType = paymentType;
        this.PaymentId = PaymentId;
    }

    public boolean isTossPayment() {
        return paymentType == PaymentType.TOSS;
    }

    public boolean isAdminPayment() {
        return paymentType == PaymentType.ADMIN;
    }
}
