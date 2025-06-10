package roomescape.domain.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import roomescape.domain.member.dto.MemberResponse;
import roomescape.domain.reservation.entity.Reservation;
import roomescape.domain.theme.dto.ThemeResponse;
import roomescape.domain.time.dto.ReservationTimeResponse;

@Schema(description = "예약 생성 응답 DTO")
public record CreateReservationResponse(
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

    public static CreateReservationResponse from(final Reservation reservation) {
        return new CreateReservationResponse(
                reservation.getId(),
                MemberResponse.from(reservation.getMember()),
                ThemeResponse.from(reservation.getTheme()),
                ReservationTimeResponse.from(reservation.getTime()),
                reservation.getDate()
        );
    }
}
