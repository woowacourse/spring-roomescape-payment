package roomescape.waiting.dto.request;

import java.time.LocalDate;

public record WaitingRequest(LocalDate date, Long timeId, Long themeId) {
    public WaitingRequest {
        if (date == null) {
            throw new NullPointerException("날짜는 null 일 수 없습니다.");
        }

        if (timeId == null) {
            throw new NullPointerException("예약 시간 번호는 null 일 수 없습니다.");
        }

        if (themeId == null) {
            throw new NullPointerException("테마 번호는 null 일 수 없습니다.");
        }
    }
}
