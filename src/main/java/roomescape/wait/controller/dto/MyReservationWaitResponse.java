package roomescape.wait.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import roomescape.wait.domain.ReservationWait;

@Schema(description = "내 예약 대기 목록 응답 정보")
public record MyReservationWaitResponse(

        @Schema(description = "예약 대기 ID", example = "101")
        Long id,

        @Schema(description = "테마 이름", example = "감옥에서 탈출하기")
        String theme,

        @Schema(description = "예약 날짜", example = "2025-07-01")
        LocalDate date,

        @Schema(description = "예약 시간", example = "15:00")
        @JsonFormat(pattern = "HH:mm")
        LocalTime time,

        @Schema(description = "대기 순번", example = "2")
        Long rank

) {
    public static MyReservationWaitResponse from(final ReservationWait reservationWait) {
        return new MyReservationWaitResponse(
                reservationWait.getId(),
                reservationWait.getSchedule().getTheme().getName().name(),
                reservationWait.getSchedule().getDate(),
                reservationWait.getSchedule().getStartAt(),
                reservationWait.calculateRank()
        );
    }

    public static List<MyReservationWaitResponse> from(final List<ReservationWait> reservationWaits) {
        return reservationWaits.stream()
                .map(MyReservationWaitResponse::from)
                .toList();
    }
}
