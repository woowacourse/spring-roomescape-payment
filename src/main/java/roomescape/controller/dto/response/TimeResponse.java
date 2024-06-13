package roomescape.controller.dto.response;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.reservation.ReservationTime;

public record TimeResponse(
        @Schema(description = "시간 고유 번호", example = "1")
        Long id,
        @Schema(description = "시간", example = "08:00")
        @JsonFormat(pattern = "HH:mm")
        LocalTime startAt
) {
    public static TimeResponse from(ReservationTime time) {
        return new TimeResponse(
                time.getId(),
                time.getStartAt()
        );
    }
}
