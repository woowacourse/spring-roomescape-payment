package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import roomescape.model.ReservationTime;

public record ReservationTimeRegisterDto(
        @Schema(description = "등록하고자 하는 예약 시각의 시각", example = "10:30")
        @NotBlank String startAt
) {

    public ReservationTime convertToTime() {
        try {
            LocalTime startTime = LocalTime.parse(startAt);
            return new ReservationTime(startTime);
        } catch (DateTimeParseException e) {
            throw new IllegalStateException("시간형식이 잘못되었습니다");
        }
    }
}
