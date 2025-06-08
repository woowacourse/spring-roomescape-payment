package roomescape.payment.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import roomescape.common.domain.DomainTerm;
import roomescape.common.validate.Validator;

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

    @Column(name = Fields.paymentType, nullable = false)
    private String paymentType;

    private Payment(final String paymentKey, final PaymentAmount amount, final String orderId, final String paymentType) {
        validate(paymentKey, amount, orderId, paymentType);
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.orderId = orderId;
        this.paymentType = paymentType;
    }

    public Payment(final Long id, final String paymentKey, final PaymentAmount amount, final String orderId, final String paymentType) {
        validate(id);
        validate(paymentKey, amount, orderId, paymentType);
        this.id = id;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.orderId = orderId;
        this.paymentType = paymentType;
    }

    public static Payment of(final String paymentKey, final PaymentAmount amount, final String orderId, final String paymentType) {
        return new Payment(paymentKey, amount, orderId, paymentType);
    }

    private static void validate(final String paymentKey, final PaymentAmount amount, final String orderId, final String paymentType) {
        Validator.of(Payment.class)
                .validateNotNull(Fields.paymentKey, paymentKey, DomainTerm.PAYMENT_KEY.label())
                .validateNotNull(Fields.orderId, orderId, DomainTerm.PAYMENT_ORDER_ID.label())
                .validateNotNull(Fields.paymentType, paymentType, DomainTerm.PAYMENT_TYPE.label())
                .validateNotNull(Fields.amount, amount, DomainTerm.PAYMENT_AMOUNT.label());
    }

    private static void validate(final Long id) {
        Validator.of(Payment.class)
                .validateNotNull(Fields.id, id, DomainTerm.PAYMENT_ID.label());
    }
}
