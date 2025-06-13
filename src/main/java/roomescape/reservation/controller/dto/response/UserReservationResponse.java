package roomescape.reservation.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.application.dto.response.UserReservationServiceResponse;

public record UserReservationResponse(
        Long id,
        String theme,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        String status,
        int rank,
        String paymentKey,
        int amount
) {

    public static UserReservationResponse from(UserReservationServiceResponse response) {
        return new UserReservationResponse(
                response.id(),
                response.themeName(),
                response.date(),
                response.time(),
                response.status(),
                response.rank(),
                response.paymentKey(),
                response.amount()
        );
    }
}
