package roomescape.mvc.time.dto;

import java.time.LocalTime;
import roomescape.mvc.time.request.ReservationTimeCreationRequest;

public record ReservationTimeCreationContent(LocalTime startAt) {

    public ReservationTimeCreationContent(ReservationTimeCreationRequest request) {
        this(request.startAt());
    }
}
