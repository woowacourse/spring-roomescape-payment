package roomescape.reservationTime.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservationTime.dto.response.ReservationTimeResponse;
import java.time.LocalTime;

@Schema(description = "예약 시간 응답")
public record ReservationTimeResponseDocs(
        @Schema(description = "시간 ID", example = "1")
        Long id,
        @Schema(description = "시작 시간", example = "10:00")
        LocalTime startAt) {

    public static ReservationTimeResponseDocs from(ReservationTimeResponse response) {
        return new ReservationTimeResponseDocs(response.id(), response.startAt());
    }
} 