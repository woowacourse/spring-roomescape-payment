package roomescape.reservation.controller.dto;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.utils.Validator;

@FieldNameConstants(level = AccessLevel.PRIVATE)
public record CreateReservationWebRequest(LocalDate date,
                                          Long timeId,
                                          Long themeId,
                                          String paymentKey,
                                          String orderId,
                                          Long amount) {

    public CreateReservationWebRequest {
        validate(date, timeId, themeId, paymentKey, orderId, amount);
    }

    private void validate(final LocalDate date, final Long timeId, final Long themeId,
        final String paymentKey, final String orderId, final Long amount) {
        Validator.of(CreateReservationWebRequest.class)
            .notNullField(Fields.date, date)
            .notNullField(Fields.timeId, timeId)
            .notNullField(Fields.themeId, themeId)
            .notNullField(Fields.paymentKey, paymentKey)
            .notNullField(Fields.orderId, orderId)
            .notNullField(Fields.amount, amount);
    }
}
