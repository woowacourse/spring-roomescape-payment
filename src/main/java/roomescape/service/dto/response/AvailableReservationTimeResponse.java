package roomescape.service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.domain.reservationtime.AvailableReservationTimeDto;

import java.time.LocalTime;

public record AvailableReservationTimeResponse(
        Long timeId,
        @JsonFormat(pattern = "HH:mm") LocalTime startAt,
        boolean alreadyBooked
) {

    public static AvailableReservationTimeResponse from(AvailableReservationTimeDto availableReservationTimeDto) {
        return new AvailableReservationTimeResponse(
                availableReservationTimeDto.id(),
                availableReservationTimeDto.startAt(),
                availableReservationTimeDto.alreadyBooked()
        );
    }
}
