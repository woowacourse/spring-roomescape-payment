package roomescape.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservation.domain.entity.MemberReservation;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "사용자 예약 응답 DTO")
public record MemberReservationResponse(
        @Schema(description = "예약 pk", example = "1") Long id,
        @Schema(description = "사용자 이름", example = "클로버") String memberName,
        @Schema(description = "예약 날짜", example = "2024-06-29") LocalDate date,
        @Schema(description = "에약 시간") LocalTime startAt,
        @Schema(description = "테마 이름", example = "공포") String themeName
) {

    public static MemberReservationResponse from(MemberReservation memberReservation) {
        return new MemberReservationResponse(
                memberReservation.getId(),
                memberReservation.getMember().getName(),
                memberReservation.getReservation().getDate(),
                memberReservation.getReservation().getTime().getStartAt(),
                memberReservation.getReservation().getTheme().getName()
        );
    }
}
