package roomescape.service.reservationtime.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalTime;
import roomescape.domain.reservationtime.ReservationTime;

public class ReservationTimeAvailableResponse {
    private final Long id;
    private final LocalTime startAt;
    private final boolean alreadyBooked;

    @JsonCreator
    public ReservationTimeAvailableResponse(Long id, LocalTime startAt, boolean alreadyBooked) {
        this.id = id;
        this.startAt = startAt;
        this.alreadyBooked = alreadyBooked;
    }

    public ReservationTimeAvailableResponse(ReservationTime reservationTime, boolean alreadyBooked) {
        this(reservationTime.getId(), reservationTime.getStartAt(), alreadyBooked);
    }

    public Long getId() {
        return id;
    }

    @JsonFormat(shape = Shape.STRING, pattern = "HH:mm")
    public LocalTime getStartAt() {
        return startAt;
    }

    public boolean getAlreadyBooked() {
        return alreadyBooked;
    }
}
