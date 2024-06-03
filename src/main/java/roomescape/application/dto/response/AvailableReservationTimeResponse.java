package roomescape.application.dto.response;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import roomescape.domain.reservation.dto.AvailableReservationTimeDto;

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
