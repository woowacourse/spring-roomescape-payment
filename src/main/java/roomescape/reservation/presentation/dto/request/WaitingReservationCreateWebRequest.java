package roomescape.reservation.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record WaitingReservationCreateWebRequest (
    @JsonFormat(pattern = "yyyy-MM-dd") @Schema(description = "예약 대기할 날짜") LocalDate date,
    @Schema(description = "예약 대기할 예약시간의 id") Long timeId,
    @Schema(description = "예약 대기할 테마의 id") Long themeId
) {
    public WaitingReservationCreateWebRequest {
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
