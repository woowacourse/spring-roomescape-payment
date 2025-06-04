package roomescape.reservationTime.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import java.util.List;
import roomescape.reservationTime.domain.ReservationTime;

@Schema(description = "예약시간 응답")
public record TimeResponse(
        @Schema(description = "예약 시간 ID")
        Long id,

        @Schema(description = "예약 시작 시간 (HH:mm 형식)")
        @JsonFormat(pattern = "HH:mm")
        LocalTime startAt
) {
    public static TimeResponse from(ReservationTime time) {
        return new TimeResponse(time.getId(), time.getStartAt());
    }

    public static List<TimeResponse> from(List<ReservationTime> times) {
        return times.stream()
                .map(TimeResponse::from)
                .toList();
    }
}
