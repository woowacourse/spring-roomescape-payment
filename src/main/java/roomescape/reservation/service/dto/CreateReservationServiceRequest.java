package roomescape.reservation.service.dto;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.utils.Validator;
import roomescape.reservation.domain.PaymentMethod;

@FieldNameConstants(level = AccessLevel.PRIVATE)
public record CreateReservationServiceRequest(
        Long memberId,
        LocalDate date,
        Long timeId,
        Long themeId,
        PaymentMethod paymentMethod
) {

    public CreateReservationServiceRequest {
        validate(memberId, date, timeId, themeId, paymentMethod);
    }

    private void validate(
            final Long memberId,
            final LocalDate date,
            final Long timeId,
            final Long themeId,
            final PaymentMethod paymentMethod
    ) {
        Validator.of(CreateReservationServiceRequest.class)
                .notNullField(Fields.memberId, memberId)
                .notNullField(Fields.date, date)
                .notNullField(Fields.timeId, timeId)
                .notNullField(Fields.themeId, themeId)
                .notNullField(Fields.paymentMethod, paymentMethod);
    }
}
