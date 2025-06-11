package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.Reservation;

@Schema(description = "대기 예약 정보 응답 객체")
public record WaitingReservationResponse(
        @Schema(description = "예약 ID", example = "1")
        Long id,

        @Schema(description = "예약자 정보")
        WaitingReservationMemberSlot member,

        @Schema(description = "테마 정보")
        WaitingReservationThemeSlot theme,

        @Schema(description = "시간 정보")
        WaitingReservationTimeSlot time,

        @Schema(description = "예약 날짜", example = "2025-06-15")
        LocalDate date
) {

    @Schema(description = "대기 예약의 회원 정보")
    public record WaitingReservationMemberSlot(
            @Schema(description = "회원 ID", example = "1")
            Long memberId,

            @Schema(description = "회원 이름", example = "김철수")
            String name
    ) {
    }

    @Schema(description = "대기 예약의 테마 정보")
    public record WaitingReservationThemeSlot(
            @Schema(description = "테마 ID", example = "1")
            Long themeId,

            @Schema(description = "테마 이름", example = "좀비 탈출")
            String themeName
    ) {
    }

    @Schema(description = "대기 예약의 시간 정보")
    public record WaitingReservationTimeSlot(
            @Schema(description = "시간 ID", example = "1")
            Long timeId,

            @Schema(description = "예약 시작 시간", example = "14:30")
            LocalTime startAt
    ) {
    }

    public static WaitingReservationResponse from(Reservation reservation) {
        return new WaitingReservationResponse(
                reservation.getId(),
                new WaitingReservationMemberSlot(
                        reservation.getMember().getId(),
                        reservation.getMember().getName()
                ),
                new WaitingReservationThemeSlot(
                        reservation.getReservationItem().getTheme().getId(),
                        reservation.getReservationItem().getTheme().getName()
                ),
                new WaitingReservationTimeSlot(
                        reservation.getReservationItem().getTime().getId(),
                        reservation.getReservationItem().getTime().getStartAt()
                ),
                reservation.getReservationItem().getDate()
        );
    }
}
