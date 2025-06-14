package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import roomescape.domain.Waiting;

@Schema(description = "예약 대기 응답 객체")
public record WaitingResponse(
        @Schema(description = "대기 ID", example = "1")
        Long id,

        @Schema(description = "회원 이름", example = "John Doe")
        String memberName,

        @Schema(description = "대기 날짜", example = "2023-12-01")
        LocalDate date,

        @Schema(description = "예약 시간 정보")
        ReservationTimeResponse time,

        @Schema(description = "테마 이름", example = "Mystery of the Lost Temple")
        String themeName
) {
    public static WaitingResponse from(Waiting waiting) {
        ReservationTimeResponse dto = ReservationTimeResponse.from(waiting.getReservationTime());
        return new WaitingResponse(
                waiting.getId(),
                waiting.getMember().getName(),
                waiting.getDate(),
                dto,
                waiting.getTheme().getName());
    }
}