package roomescape.domain.waiting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import roomescape.domain.member.dto.MemberResponse;
import roomescape.domain.theme.dto.ThemeResponse;
import roomescape.domain.time.dto.ReservationTimeResponse;
import roomescape.domain.waiting.entity.Waiting;

@Schema(description = "예약 대기 생성 응답 DTO")
public record CreateWaitingResponse(
        @Schema(description = "예약 대기 ID", example = "1")
        Long id,

        @Schema(description = "예약 날짜", example = "2026-06-07")
        LocalDate date,

        @Schema(description = "회원 정보")
        MemberResponse member,

        @Schema(description = "예약 시간 정보")
        ReservationTimeResponse time,

        @Schema(description = "테마 정보")
        ThemeResponse theme
) {
    public static CreateWaitingResponse from(final Waiting waiting) {
        return new CreateWaitingResponse(
                waiting.getId(),
                waiting.getDate(),
                MemberResponse.from(waiting.getMember()),
                ReservationTimeResponse.from(waiting.getTime()),
                ThemeResponse.from(waiting.getTheme())
        );
    }

}
