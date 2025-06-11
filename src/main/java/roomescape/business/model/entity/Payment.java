package roomescape.business.model.entity;

import jakarta.persistence.CascadeType;
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
import roomescape.exception.payment.PaymentIntegrityViolationException;

@EqualsAndHashCode(of = "id")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Payment {

    @EmbeddedId
    private final Id id;

    @Setter
    private String paymentKey;

    private Long amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Setter
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Reservation reservation;

    protected Payment() {
        id = Id.issue();
    }

    public static Payment create(Reservation reservation) {
        Long amount = reservation.getTheme().getPrice();
        return new Payment(Id.issue(), null, amount, PaymentStatus.IN_PROGRESS, reservation);
    }

    public static Payment restore(String id, String paymentKey, Long amount, PaymentStatus status,
                                  Reservation reservation) {
        return new Payment(Id.create(id), paymentKey, amount, status, reservation);
    }

    public void approve(String paymentKey, Long amount) {
        validateAmount(amount);
        this.paymentKey = paymentKey;
        this.status = PaymentStatus.DONE;
        reservation.confirm();
    }

    private void validateAmount(Long amount) {
        if (!this.amount.equals(amount)) {
            throw new PaymentIntegrityViolationException();
        }
    }

}



