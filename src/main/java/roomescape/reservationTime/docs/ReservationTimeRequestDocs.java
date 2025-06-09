package roomescape.reservationTime.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservationTime.dto.request.ReservationTimeRequest;
import java.time.LocalTime;

@Schema(description = "예약 시간 요청")
public record ReservationTimeRequestDocs(
        @Schema(description = "시작 시간", example = "10:00")
        LocalTime startAt) {

    public ReservationTimeRequest toReservationTimeRequest() {
        return new ReservationTimeRequest(startAt);
    }
} 