package roomescape.reservation.service.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.controller.dto.WaitingResponse;

public record MyReservationInfo(long id,
                                String themeName,
                                LocalDate date,
                                LocalTime time,
                                WaitingResponse waitingResponse) {
}
