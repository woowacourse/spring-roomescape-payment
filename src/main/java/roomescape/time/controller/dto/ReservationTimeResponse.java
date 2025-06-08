package roomescape.time.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import java.util.List;
import roomescape.time.domain.ReservationTime;

@Schema(description = "예약 시간 응답 정보")
public record ReservationTimeResponse(

        @Schema(description = "예약 시간 ID", example = "1")
        Long id,

        @Schema(description = "예약 시작 시간", example = "10:30")
        @JsonFormat(pattern = "HH:mm")
        LocalTime startAt

) {
    public static List<ReservationTimeResponse> from(final List<ReservationTime> reservationTimes) {
        return reservationTimes.stream()
                .map(ReservationTimeResponse::from)
                .toList();
    }

    public static ReservationTimeResponse from(final ReservationTime reservationTime) {
        return new ReservationTimeResponse(
                reservationTime.getId(),
                reservationTime.getStartAt()
        );
    }
}
