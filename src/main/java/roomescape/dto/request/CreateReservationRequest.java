package roomescape.dto.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record CreateReservationRequest(
        long memberId,
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        long themeId,
        long timeId
) {
}
