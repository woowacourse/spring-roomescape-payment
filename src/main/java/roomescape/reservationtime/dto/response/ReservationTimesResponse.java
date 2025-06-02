package roomescape.reservationtime.dto.response;

import java.util.List;

public record ReservationTimesResponse(List<ReservationTimeResponse> data
) {
    public static ReservationTimesResponse of(List<ReservationTimeResponse> data) {
        return new ReservationTimesResponse(data);
    }
}
