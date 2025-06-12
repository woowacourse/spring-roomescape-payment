package roomescape.time.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.utils.Validator;

@Schema(description = "예약 시간 정보 응답")
@FieldNameConstants(level = AccessLevel.PRIVATE)
public record ReservationTimeWebResponse(
        @Schema(description = "예약 시간 ID", example = "1")
        Long id,
        @Schema(description = "예약 시작 시간", example = "14:00")
        LocalTime startAt
) {

    public ReservationTimeWebResponse {
        validate(id, startAt);
    }

    private void validate(final Long id, final LocalTime startAt) {
        Validator.of(ReservationTimeWebResponse.class)
                .notNullField(ReservationTimeWebResponse.Fields.id, id)
                .notNullField(ReservationTimeWebResponse.Fields.startAt, startAt);
    }
}
