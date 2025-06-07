package roomescape.timeslot.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record TimeSlotConditionRequest(
        @NotNull(message = "예약 날짜는 필수입니다.") LocalDate date,
        @NotNull(message = "예약 테마 번호는 필수입니다.") Long themeId
) {
}
