package roomescape.waiting.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservation.service.dto.response.ReservationTimeResponse;
import roomescape.theme.service.dto.response.ThemeResponse;
import roomescape.waiting.domain.Waiting;

import java.time.LocalDate;

@Schema(name = "CreateWaitingResponse(예약 대기 생성 응답 DTO)")
public record CreateWaitingResponse(
        Long id,
        LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme
) {
    public static CreateWaitingResponse from(Waiting waiting) {
        return new CreateWaitingResponse(
                waiting.getId(),
                waiting.getDate(),
                ReservationTimeResponse.from(waiting.getTime()),
                ThemeResponse.from(waiting.getTheme())
        );
    }
}
