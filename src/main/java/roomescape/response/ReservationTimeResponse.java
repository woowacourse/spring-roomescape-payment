package roomescape.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.model.ReservationTime;

import java.time.LocalTime;

public class ReservationTimeResponse {

    private final Long id;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startAt;

    public ReservationTimeResponse(final Long id, final LocalTime startAt) {
        this.id = id;
        this.startAt = startAt;
    }

    public static ReservationTimeResponse of(final ReservationTime domain) {
        return new ReservationTimeResponse(domain.getId(), domain.getStartAt());
    }

    public Long getId() {
        return id;
    }

    public LocalTime getStartAt() {
        return startAt;
    }
}
