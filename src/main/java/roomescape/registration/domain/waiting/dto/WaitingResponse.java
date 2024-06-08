package roomescape.registration.domain.waiting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.registration.domain.reservation.domain.Reservation;
import roomescape.registration.domain.waiting.domain.Waiting;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "대기 응답")
public record WaitingResponse(

        @Schema(description = "대기 ID", example = "1")
        long id,

        @Schema(description = "멤버 이름", example = "홍길동")
        String memberName,

        @Schema(description = "테마 이름", example = "홍길동전")
        String themeName,

        @Schema(description = "대기 날짜", example = "2099-12-31")
        LocalDate date,

        @Schema(description = "시작 시간", example = "14:00")
        LocalTime startAt
) {
    public static WaitingResponse from(Waiting waiting) {
        Reservation reservation = waiting.getReservation();

        return new WaitingResponse(
                waiting.getId(),
                waiting.getReservation().getMember().getName(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getReservationTime().getStartAt()
        );
    }
}
