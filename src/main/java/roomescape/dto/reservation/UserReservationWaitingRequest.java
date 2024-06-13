package roomescape.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.Objects;

public record UserReservationWaitingRequest(
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        Long timeId,
        Long themeId
) {

    public UserReservationWaitingRequest {
        Objects.requireNonNull(date);
        Objects.requireNonNull(timeId);
        Objects.requireNonNull(themeId);
    }

    public ReservationRequest toReservationRequest(Long memberId) {
        return new ReservationRequest(
                date,
                timeId,
                themeId,
                memberId
        );
    }
}
