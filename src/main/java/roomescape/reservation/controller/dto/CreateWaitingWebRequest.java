package roomescape.reservation.controller.dto;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.utils.Validator;

@FieldNameConstants(level = AccessLevel.PRIVATE)
public record CreateWaitingWebRequest(LocalDate date,
                                      Long timeId,
                                      Long themeId) {

    public CreateWaitingWebRequest {
        validate(date, timeId, themeId);
    }

    private void validate(final LocalDate date, final Long timeId, final Long themeId) {
        Validator.of(CreateWaitingWebRequest.class)
                .notNullField(Fields.date, date)
                .notNullField(Fields.timeId, timeId)
                .notNullField(Fields.themeId, themeId);
    }
}
