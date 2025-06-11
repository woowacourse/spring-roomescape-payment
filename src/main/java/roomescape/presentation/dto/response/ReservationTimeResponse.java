package roomescape.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.ReservationTime;

import java.time.LocalTime;
import java.util.List;

@Schema(description = "예약 시간 정보 응답 DTO")
public record ReservationTimeResponse(
        @Schema(description = "예약 시간 ID")
        Long id,

        @Schema(description = "예약 시작 시간", example = "14:00")
        @JsonFormat(pattern = "HH:mm")
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
