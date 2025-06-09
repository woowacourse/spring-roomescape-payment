package roomescape.domain.time.dto;

import java.time.LocalTime;
import roomescape.domain.time.request.ReservationTimeCreationRequest;

public record ReservationTimeCreationContent(LocalTime startAt) {

    public ReservationTimeCreationContent(ReservationTimeCreationRequest request) {
        this(request.startAt());
    }
}
