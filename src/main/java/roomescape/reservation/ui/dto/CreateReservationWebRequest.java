package roomescape.reservation.ui.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.domain.DomainTerm;
import roomescape.common.validate.Validator;

import java.time.LocalDate;

@FieldNameConstants(level = AccessLevel.PRIVATE)
public record CreateReservationWebRequest(LocalDate date,
                                          Long timeId,
                                          Long themeId,
                                          String paymentKey,
                                          String orderId,
                                          int amount,
                                          String paymentType
) {

    public CreateReservationWebRequest {
        validate(date, timeId, themeId, paymentKey, orderId, amount, paymentType);
    }

    public CreateReservationWithUserIdWebRequest toRequestWithUserId(final Long userId) {
        return new CreateReservationWithUserIdWebRequest(
                date,
                timeId,
                themeId,
                userId,
                paymentKey,
                orderId,
                amount,
                paymentType
        );
    }

    private void validate(final LocalDate date, final Long timeId, final Long themeId,
                          final String paymentKey, final String orderId, final int amount, final String paymentType) {
        Validator.of(CreateReservationWithUserIdWebRequest.class)
                .validateNotNull(Fields.date, date, DomainTerm.RESERVATION_DATE.label())
                .validateNotNull(Fields.timeId, timeId, DomainTerm.RESERVATION_TIME_ID.label())
                .validateNotNull(Fields.themeId, themeId, DomainTerm.THEME_ID.label())
                .validateNotNull(Fields.paymentKey, paymentKey, DomainTerm.PAYMENT_KEY.label())
                .validateNotNull(Fields.orderId, orderId, DomainTerm.PAYMENT_ORDER_ID.label())
                .validateNotNull(Fields.amount, amount, DomainTerm.PAYMENT_AMOUNT.label())
                .validateNotNull(Fields.paymentType, paymentType, DomainTerm.PAYMENT_TYPE.label());

    }
}
