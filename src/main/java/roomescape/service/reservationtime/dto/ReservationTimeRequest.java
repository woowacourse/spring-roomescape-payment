package roomescape.service.reservationtime.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import jakarta.validation.constraints.NotNull;
import roomescape.domain.reservationtime.ReservationTime;

import java.time.LocalTime;

public class ReservationTimeRequest {
    @NotNull(message = "startAt 값이 null일 수 없습니다.")
    private final LocalTime startAt;

    @JsonCreator
    public ReservationTimeRequest(LocalTime startAt) {
        this.startAt = startAt;
    }

    public ReservationTime toReservationTime() {
        return new ReservationTime(startAt);
    }

    @JsonFormat(shape = Shape.STRING, pattern = "HH:mm")
    public LocalTime getStartAt() {
        return startAt;
    }
}
