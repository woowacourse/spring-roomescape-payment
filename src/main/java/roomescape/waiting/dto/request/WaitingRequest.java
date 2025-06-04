package roomescape.waiting.dto.request;

import java.time.LocalDate;
import roomescape.common.exception.InvalidReservationException;

public record WaitingRequest(LocalDate date, Long timeId, Long themeId) {
    public WaitingRequest {
        if (date == null) {
            throw new InvalidReservationException("날짜는 비어있을 수 없습니다.");
        }

        if (timeId == null) {
            throw new InvalidReservationException("예약 시간 번호는 비어있을 수 없습니다.");
        }

        if (themeId == null) {
            throw new InvalidReservationException("테마 번호는 비어있을 수 없습니다.");
        }
    }
}
