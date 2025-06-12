package roomescape.reservation.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservation.repository.dto.ReservationTimeWithBooked;

@Schema(name = "ReservationTimeWithBookedResponse(예약된 시간 조회 응답 DTO)")
public record ReservationTimeWithBookedResponse(
        ReservationTimeResponse timeResponse,
        boolean alreadyBooked
) {
    public static ReservationTimeWithBookedResponse from(ReservationTimeWithBooked dataResponse) {
        return new ReservationTimeWithBookedResponse(
                ReservationTimeResponse.from(dataResponse.time()),
                dataResponse.booked()
        );
    }
}
