package roomescape.reservation.dto.response;

import roomescape.time.dto.response.ReservationTimeResponse;

public record BookedReservationTimeResponse(
        ReservationTimeResponse timeResponse,
        boolean alreadyBooked
) {
}
