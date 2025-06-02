package roomescape.reservation.dto.response;

import java.util.List;

public record MyReservationsResponse(List<MyReservationResponse> data
) {
    public static MyReservationsResponse of(List<MyReservationResponse> data) {
        return new MyReservationsResponse(data);
    }
}
