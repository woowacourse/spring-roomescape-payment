package roomescape.business.model.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import roomescape.business.model.vo.Id;
import roomescape.business.model.vo.PaymentStatus;
import roomescape.exception.ErrorCode;
import roomescape.exception.RootBusinessException;

@EqualsAndHashCode(of = "id")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Payment {

    @EmbeddedId
    private final Id id;

    private String orderId;

    @Setter
    private String paymentKey;

    private Long amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    protected Payment() {
        id = Id.issue();
    }

    public static Payment create(String orderId, Long amount) {
        return new Payment(Id.issue(), orderId, null, amount, PaymentStatus.IN_PROGRESS, null);
    }

    public static Payment restore(String id, String orderId, String paymentKey, Long amount, PaymentStatus status,
                                  Reservation reservation) {
        return new Payment(Id.create(id), orderId, paymentKey, amount, status, reservation);
    }

    public void approve(String paymentKey, Long amount, Reservation reservation) {
        validateAmount(amount);
        this.paymentKey = paymentKey;
        this.status = PaymentStatus.DONE;
        this.reservation = reservation;
    }

    private void validateAmount(Long amount) {
        if (!this.amount.equals(amount)) {
            throw new RootBusinessException(ErrorCode.INVALID_PAYMENT_AMOUNT) {
            };
        }
    }

}



