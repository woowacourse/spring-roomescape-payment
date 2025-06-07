package roomescape.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import roomescape.domain.member.dto.MemberResponse;
import roomescape.domain.theme.dto.ThemeResponse;
import roomescape.domain.time.dto.ReservationTimeResponse;
import roomescape.domain.waiting.entity.Waiting;

@Schema(description = "예약 대기 응답 DTO")
public record ReservationWaitingResponse(
        @Schema(description = "예약 대기 ID", example = "1")
        Long id,

        @Schema(description = "회원 정보")
        MemberResponse name,

        @Schema(description = "테마 정보")
        ThemeResponse theme,

        @Schema(description = "예약 날짜", example = "2026-06-07")
        LocalDate date,

        @Schema(description = "예약 시작 시간 정보")
        ReservationTimeResponse startAt
) {

    public static ReservationWaitingResponse from(final Waiting waiting) {
        return new ReservationWaitingResponse(
                waiting.getId(),
                MemberResponse.from(waiting.getMember()),
                ThemeResponse.from(waiting.getTheme()),
                waiting.getDate(),
                ReservationTimeResponse.from(waiting.getTime())
        );
    }
}
