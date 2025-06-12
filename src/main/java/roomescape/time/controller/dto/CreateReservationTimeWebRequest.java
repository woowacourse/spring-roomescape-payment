package roomescape.time.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.utils.Validator;

@Schema(description = "예약 시간 정보 요청")
@FieldNameConstants(level = AccessLevel.PRIVATE)
public record CreateReservationTimeWebRequest(
        @Schema(description = "예약 시작 시간", example = "14:00")
        LocalTime startAt
) {

    public CreateReservationTimeWebRequest {
        validate(startAt);
    }

    private void validate(final LocalTime startAt) {
        Validator.of(CreateReservationTimeWebRequest.class)
                .notNullField(Fields.startAt, startAt);
    }
}
