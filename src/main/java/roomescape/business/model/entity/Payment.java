package roomescape.business.model.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import roomescape.business.dto.PaymentApproveDto;
import roomescape.business.model.vo.Id;

@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
@Getter
@Entity
@Table(name = "payment")
public class Payment {

    @EmbeddedId
    private final Id id;

    @OneToOne(optional = false)
    private Reservation reservation;
    private String paymentKey;
    private String orderId;
    private Long amount;

    protected Payment() {
        id = Id.issue();
    }

    public static Payment create(final Reservation reservation, final String paymentKey, final String orderId,
                                 final Long amount) {
        return new Payment(Id.issue(), reservation, paymentKey, orderId, amount);
    }

    public static Payment create(final Reservation reservation, final PaymentApproveDto paymentApproveDto) {
        return new Payment(Id.issue(), reservation, paymentApproveDto.paymentKey(), paymentApproveDto.orderId(),
                paymentApproveDto.amount());
    }

    public static Payment restore(final String id, final Reservation reservation, final String paymentKey,
                                  final String orderId, final Long amount) {
        return new Payment(Id.create(id), reservation, paymentKey, orderId, amount);
    }

}
