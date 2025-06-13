package roomescape.payment.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import roomescape.common.domain.DomainTerm;
import roomescape.common.validate.Validator;
import roomescape.reservation.domain.Reservation;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldNameConstants(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = Fields.paymentKey, nullable = false)
    private String paymentKey;

    @Embedded
    @AttributeOverride(
            name = PaymentAmount.Fields.value,
            column = @Column(name = Fields.amount, nullable = false))
    private PaymentAmount amount;

    @Column(name = Fields.orderId, nullable = false)
    private String orderId;

    @OneToOne(optional = false)
    private Reservation reservation;

    private Payment(final String paymentKey, final PaymentAmount amount, final String orderId, final Reservation reservation) {
        validate(paymentKey, amount, orderId, reservation);
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.orderId = orderId;
        this.reservation = reservation;
    }

    public Payment(final Long id, final String paymentKey, final PaymentAmount amount, final String orderId, final Reservation reservation) {
        validate(id);
        validate(paymentKey, amount, orderId, reservation);
        this.id = id;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.orderId = orderId;
        this.reservation = reservation;
    }

    public static Payment of(final String paymentKey,
                             final PaymentAmount amount,
                             final String orderId,
                             final Reservation reservation
    ) {
        return new Payment(paymentKey, amount, orderId, reservation);
    }

    private static void validate(final String paymentKey,
                                 final PaymentAmount amount,
                                 final String orderId,
                                 final Reservation reservation
    ) {
        Validator.of(Payment.class)
                .validateNotNull(Fields.paymentKey, paymentKey, DomainTerm.PAYMENT_KEY.label())
                .validateNotNull(Fields.orderId, orderId, DomainTerm.PAYMENT_ORDER_ID.label())
                .validateNotNull(Fields.amount, amount, DomainTerm.PAYMENT_AMOUNT.label())
                .validateNotNull(Fields.reservation, reservation, DomainTerm.RESERVATION.label());
    }

    private static void validate(final Long id) {
        Validator.of(Payment.class)
                .validateNotNull(Fields.id, id, DomainTerm.PAYMENT_ID.label());
    }
}
