package roomescape.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import roomescape.domain.member.dto.MemberResponse;
import roomescape.domain.reservation.entity.Reservation;
import roomescape.domain.theme.dto.ThemeResponse;
import roomescape.domain.time.dto.ReservationTimeResponse;

@Schema(description = "관리자 예약 응답 DTO")
public record AdminReservationResponse(
        @Schema(description = "예약 ID", example = "1")
        Long id,

        @Schema(description = "회원 정보")
        MemberResponse member,

        @Schema(description = "테마 정보")
        ThemeResponse theme,

        @Schema(description = "예약 시간 정보")
        ReservationTimeResponse time,

        @Schema(description = "예약 날짜", example = "2026-06-07")
        LocalDate date
) {
    public static AdminReservationResponse from(final Reservation reservation) {
        return new AdminReservationResponse(
                reservation.getId(),
                MemberResponse.from(reservation.getMember()),
                ThemeResponse.from(reservation.getTheme()),
                ReservationTimeResponse.from(reservation.getTime()),
                reservation.getDate()
        );
    }
}
