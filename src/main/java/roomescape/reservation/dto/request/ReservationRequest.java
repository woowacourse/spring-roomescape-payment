package roomescape.reservation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import roomescape.reservation.domain.ReservationInfo;

public record ReservationRequest(
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        Long timeId,
        Long themeId
) {

    public static ReservationRequest from(final ReservationInfo info) {
        return new ReservationRequest(info.getDate(), info.getIdOfTime(), info.getIdOfTheme());
    }

    public ReservationRequest {
        if (date == null) {
            throw new IllegalArgumentException("날짜는 반드시 입력해야합니다.");
        }
        if (timeId == null) {
            throw new IllegalArgumentException("timeId는 반드시 입력해야합니다.");
        }
        if (themeId == null) {
            throw new IllegalArgumentException("themeId는 반드시 입력해야합니다.");
        }
    }

}
