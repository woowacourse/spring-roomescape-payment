package roomescape.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.ReservationTime;

import java.time.LocalTime;
import java.util.List;

public record ReservationTimeResponse(
        @Schema(example = "1")
        Long id,

        @JsonFormat(pattern = "HH:mm")
        @Schema(example = "09:00")
        LocalTime startAt
) {

    public static List<ReservationTimeResponse> from(List<ReservationTime> reservationTimes) {
        return reservationTimes.stream()
                .map(ReservationTimeResponse::from)
                .toList();
    }

    public static ReservationTimeResponse from(ReservationTime reservationTime) {
        return new ReservationTimeResponse(
                reservationTime.getId(),
                reservationTime.getStartAt()
        );
    }
}
