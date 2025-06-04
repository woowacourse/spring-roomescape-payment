package roomescape.timeslot.dto.request;

import java.time.LocalDate;

public record TimeSlotConditionRequest(LocalDate date, Long themeId) {

    public TimeSlotConditionRequest {
        if (date == null) {
            throw new IllegalArgumentException("date는 null 일 수 없습니다.");
        }

        if (themeId == null) {
            throw new IllegalArgumentException("theme id는 null 일 수 없습니다.");
        }
    }
}
