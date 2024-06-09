package roomescape.reservation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record WaitingRequest(
        @NotNull(message = "예약 날짜는 null일 수 없습니다.")
        LocalDate date,
        @NotNull(message = "예약 요청의 timeId는 null일 수 없습니다.")
        Long timeId,
        @NotNull(message = "예약 요청의 themeId는 null일 수 없습니다.")
        Long themeId
) {
}
