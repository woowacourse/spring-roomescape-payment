package roomescape.domain.payment;

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
public class Payment extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    public Payment(final PaymentType type) {
        this.paymentType = type;
    }

    public boolean isTossPayment() {
        return paymentType == PaymentType.TOSS;
    }

    public boolean isAdminPayment() {
        return paymentType == PaymentType.ADMIN;
    }
}
