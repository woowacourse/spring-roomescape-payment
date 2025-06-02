package roomescape.payment.service.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.utils.Validator;

@FieldNameConstants(level = AccessLevel.PRIVATE)
public record PaymentConfirmRequest(
    String paymentKey,
    String orderId,
    Long amount
) {

    public PaymentConfirmRequest {
        validate(paymentKey, orderId, amount);
    }

    private void validate(final String paymentKey, final String orderId, final Long amount) {
        Validator.of(PaymentConfirmRequest.class)
            .notNullField(Fields.paymentKey, paymentKey)
            .notNullField(Fields.orderId, orderId)
            .notNullField(Fields.amount, amount);
    }
}
