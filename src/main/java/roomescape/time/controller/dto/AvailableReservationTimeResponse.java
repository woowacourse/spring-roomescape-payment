package roomescape.time.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import java.util.List;
import roomescape.time.domain.AvailableReservationTime;

@Schema(description = "예약 가능한 시간 응답 정보")
public record AvailableReservationTimeResponse(

        @Schema(description = "예약 시간 ID", example = "3")
        Long id,

        @Schema(description = "예약 시작 시간", example = "13:00")
        @JsonFormat(pattern = "HH:mm")
        LocalTime startAt,

        @Schema(description = "이미 예약되었는지 여부", example = "false")
        boolean isReserved

) {
    public static AvailableReservationTimeResponse from(final AvailableReservationTime availableReservationTime) {
        return new AvailableReservationTimeResponse(
                availableReservationTime.schedule().getReservationTime().getId(),
                availableReservationTime.schedule().getStartAt(),
                availableReservationTime.available()
        );
    }

    public static List<AvailableReservationTimeResponse> from(
            final List<AvailableReservationTime> availableReservationTimes
    ) {
        return availableReservationTimes.stream().map(AvailableReservationTimeResponse::from).toList();
    }
}
