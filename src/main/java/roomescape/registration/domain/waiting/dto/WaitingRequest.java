package roomescape.registration.domain.waiting.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;

@Tag(name = "예약 대기 요청", description = "예약 대기 신청에 필요한 정보를 요청한다.")
public record WaitingRequest(
        LocalDate date,
        long timeId,
        long themeId
) {
}
