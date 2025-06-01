package roomescape.reservation.dto.response;

import java.util.List;

public record ReservationsResponse(
    List<ReservationResponse> data
) {
    public static ReservationsResponse of(List<ReservationResponse> data) {
        return new ReservationsResponse(data);
    }
} 