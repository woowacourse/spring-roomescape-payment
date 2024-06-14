package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.entity.ReservationTime;

import java.time.LocalTime;

@Schema(description = "예약 가능한 시간 요청 DTO 입니다.")
public record AvailableTimeResponse(
        @Schema(description = "예약 시간 ID 입니다.")
        long id,
        @Schema(description = "예약 시작 시간입니다.")
        LocalTime startAt,
        @Schema(description = "예약 여부입니다.")
        boolean isBooked
) {
    public static AvailableTimeResponse of(ReservationTime reservationTime, boolean isReserved) {
        return new AvailableTimeResponse(reservationTime.getId(), reservationTime.getStartAt(), isReserved);
    }
}
