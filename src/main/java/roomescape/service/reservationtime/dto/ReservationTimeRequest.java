package roomescape.service.reservationtime.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalTime;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.exception.common.InvalidRequestBodyException;

public class ReservationTimeRequest {
    private final LocalTime startAt;

    @JsonCreator
    public ReservationTimeRequest(LocalTime startAt) {
        validate(startAt);
        this.startAt = startAt;
    }

    private void validate(LocalTime startAt) {
        if (startAt == null) {
            throw new InvalidRequestBodyException();
        }
    }

    public ReservationTime toReservationTime() {
        return new ReservationTime(startAt);
    }

    @JsonFormat(shape = Shape.STRING, pattern = "HH:mm")
    public LocalTime getStartAt() {
        return startAt;
    }
}