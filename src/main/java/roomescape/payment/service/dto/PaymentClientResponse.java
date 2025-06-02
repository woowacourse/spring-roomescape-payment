package roomescape.payment.service.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.utils.Validator;

@FieldNameConstants(level = AccessLevel.PRIVATE)
public record PaymentClientResponse(
    String paymentKey,
    String orderId,
    Long totalAmount,
    String status,
    String requestedAt,
    String approvedAt
) {

    public PaymentClientResponse {
        validate(paymentKey, orderId, totalAmount, status, requestedAt, approvedAt);
    }

    private void validate(
        final String paymentKey,
        final String orderId,
        final Long totalAmount,
        final String status,
        final String requestedAt,
        final String approvedAt
    ) {
        Validator.of(PaymentClientResponse.class)
            .notNullField(Fields.paymentKey, paymentKey)
            .notNullField(Fields.orderId, orderId)
            .notNullField(Fields.totalAmount, totalAmount)
            .notNullField(Fields.status, status)
            .notNullField(Fields.requestedAt, requestedAt)
            .notNullField(Fields.approvedAt, approvedAt);
    }
}
