package roomescape.waiting.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import roomescape.member.application.dto.MemberResponse;
import roomescape.reservationTime.application.dto.TimeResponse;
import roomescape.theme.application.dto.ThemeResponse;
import roomescape.waiting.domain.Waiting;

@Schema(description = "대기 정보 응답")
public record WaitingResponse(
        @Schema(description = "대기 ID")
        Long id,

        @Schema(description = "회원 정보")
        MemberResponse member,

        @Schema(description = "테마 정보")
        ThemeResponse theme,

        @Schema(description = "예약 날짜", pattern = "yyyy-MM-dd")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,

        @Schema(description = "예약 시간 정보")
        TimeResponse time
) {
    public static WaitingResponse from(Waiting waiting) {
        return new WaitingResponse(waiting.getId(), MemberResponse.from(waiting.getMember()),
                ThemeResponse.from(waiting.getTheme()), waiting.getDate(),
                TimeResponse.from(waiting.getTime()));
    }

    public static List<WaitingResponse> from(List<Waiting> waitings) {
        return waitings.stream()
                .map(WaitingResponse::from)
                .toList();
    }
}
