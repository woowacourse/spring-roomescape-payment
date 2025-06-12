package roomescape.reservation.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.utils.Validator;

@Schema(description = "예약 가능한 시간 응답")
@FieldNameConstants(level = AccessLevel.PRIVATE)
public record AvailableReservationTimeWebResponse(
        @Schema(description = "예약 시간", example = "14:00")
        LocalTime startAt,
        @Schema(description = "예약 시간 ID", example = "1")
        Long timeId,
        @Schema(description = "예약 여부", example = "true")
        Boolean isBooked
) {

    public AvailableReservationTimeWebResponse {
        validate(startAt, timeId, isBooked);
    }

    private void validate(final LocalTime startAt, final Long timeId, final Boolean isBooked) {
        Validator.of(AvailableReservationTimeWebResponse.class)
                .notNullField(Fields.startAt, startAt)
                .notNullField(Fields.timeId, timeId)
                .notNullField(Fields.isBooked, isBooked);
    }
}
