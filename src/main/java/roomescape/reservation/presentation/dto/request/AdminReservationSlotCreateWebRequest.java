package roomescape.reservation.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record AdminReservationSlotCreateWebRequest(
        @JsonFormat(pattern = "yyyy-MM-dd") @Schema(description = "예약을 생성할 날짜") LocalDate date,
        @Schema(description = "예약할 예약시간의 id") Long timeId,
        @Schema(description = "예약할 테마의 id") Long themeId,
        @Schema(description = "예약자 id") Long memberId
) {
    public AdminReservationSlotCreateWebRequest {
        if (date == null) {
            throw new IllegalArgumentException("날짜는 반드시 입력해야합니다.");
        }
        if (timeId == null) {
            throw new IllegalArgumentException("timeId는 반드시 입력해야합니다.");
        }
        if (themeId == null) {
            throw new IllegalArgumentException("themeId는 반드시 입력해야합니다.");
        }
        if (memberId == null) {
            throw new IllegalArgumentException("memberId는 반드시 입력해야합니다.");
        }
    }
}
