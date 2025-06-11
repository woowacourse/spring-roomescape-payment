package roomescape.wait.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import roomescape.theme.controller.dto.ThemeResponse;
import roomescape.time.controller.dto.ReservationTimeResponse;
import roomescape.wait.domain.ReservationWait;

@Schema(description = "예약 대기 조회 응답 정보")
public record ReservationWaitResponse(

        @Schema(description = "예약 대기 ID", example = "101")
        Long id,

        @Schema(description = "회원 이름", example = "홍길동")
        String name,

        @Schema(description = "예약 날짜", example = "2025-07-01")
        LocalDate date,

        @Schema(description = "예약 시간 정보")
        ReservationTimeResponse time,

        @Schema(description = "테마 정보")
        ThemeResponse theme,

        @Schema(description = "대기 순번", example = "1")
        Long rank

) {
    public static ReservationWaitResponse from(final ReservationWait reservationWait) {
        return new ReservationWaitResponse(
                reservationWait.getId(),
                reservationWait.getMember().getName().name(),
                reservationWait.getSchedule().getDate(),
                ReservationTimeResponse.from(reservationWait.getSchedule().getReservationTime()),
                ThemeResponse.from(reservationWait.getSchedule().getTheme()),
                reservationWait.calculateRank()
        );
    }

    public static List<ReservationWaitResponse> from(final List<ReservationWait> reservationWaits) {
        return reservationWaits.stream()
                .map(ReservationWaitResponse::from)
                .toList();
    }
}
