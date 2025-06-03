package roomescape.payment.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.domain.DomainTerm;
import roomescape.common.validate.Validator;

@FieldNameConstants(level = AccessLevel.PRIVATE)

public record PaymentResult(String paymentKey,
                            int amount,
                            String orderId,
                            String paymentType) {

    public PaymentResult {
        validate(paymentKey, orderId, amount, paymentType);
    }

    public boolean verifyPayment(PaymentRequest request, PaymentResult paymentResult) {
        return paymentResult.orderId().equals(request.orderId())
                && paymentResult.paymentKey().equals(request.paymentKey())
                && paymentResult.amount() == request.amount();
    }

    private void validate(final String paymentKey, final String orderId, final int amount, final String paymentType) {
        Validator.of(PaymentResult.class)
                .validateNotNull(Fields.paymentKey, paymentKey, DomainTerm.PAYMENT_KEY.label())
                .validateNotNull(Fields.orderId, orderId, DomainTerm.PAYMENT_ORDER_ID.label())
                .validateNotNull(Fields.amount, amount, DomainTerm.PAYMENT_AMOUNT.label())
                .validateNotNull(Fields.paymentType, paymentType, DomainTerm.PAYMENT_TYPE.label());
    }
}
