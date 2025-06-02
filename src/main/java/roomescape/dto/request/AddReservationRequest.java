package roomescape.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AddReservationRequest(
        LocalDate date,
        Long timeId,
        Long themeId
) {
    public static AddReservationRequest from(CreateReservationRequest createReservationRequest) {
        return new AddReservationRequest(
                createReservationRequest.date(),
                createReservationRequest.timeId(),
                createReservationRequest.themeId()
        );
    }
}
