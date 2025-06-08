package roomescape.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Waiting;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "대기 정보 응답 DTO")
public record WaitingResponse(
        @Schema(description = "대기 ID")
        Long id,

        @Schema(description = "대기자 정보")
        MemberResponse member,

        @Schema(description = "대기 테마 정보")
        ThemeResponse theme,

        @Schema(description = "대기 일자", example = "2024-03-20")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,

        @Schema(description = "대기 시간 정보")
        ReservationTimeResponse time
) {

    public static WaitingResponse from(Waiting waiting) {
        return new WaitingResponse(
                waiting.getId(),
                MemberResponse.from(waiting.getMember()),
                ThemeResponse.from(waiting.getReservationInfo().getTheme()),
                waiting.getReservationInfo().getDate(),
                ReservationTimeResponse.from(waiting.getReservationInfo().getTime())
        );
    }

    public static List<WaitingResponse> from(List<Waiting> waitings) {
        return waitings.stream()
                .map(WaitingResponse::from)
                .toList();
    }
}
