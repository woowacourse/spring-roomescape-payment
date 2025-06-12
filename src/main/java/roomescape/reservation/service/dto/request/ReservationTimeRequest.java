package roomescape.reservation.service.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservation.domain.ReservationTime;

import java.time.LocalTime;

@Schema(name = "ReservationTimeRequest(예약 시간 생성 요청 DTO)")
public record ReservationTimeRequest(
        @JsonFormat(pattern = "HH:mm") LocalTime startAt
) {

    public ReservationTime toEntity() {
        return new ReservationTime(startAt);
    }
}
