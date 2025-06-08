package roomescape.reservation.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import roomescape.member.application.dto.MemberResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservationTime.application.dto.TimeResponse;
import roomescape.theme.application.dto.ThemeResponse;

@Schema(description = "예약 응답 DTO")
public record ReservationResponse(
        @Schema(description = "예약 ID")
        Long id,

        @Schema(description = "예약한 회원 정보")
        MemberResponse member,

        @Schema(description = "예약한 테마 정보")
        ThemeResponse theme,

        @Schema(description = "예약한 날짜", type = "string", format = "date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,

        @Schema(description = "예약 시간 정보")
        TimeResponse time
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(reservation.getId(), MemberResponse.from(reservation.getMember()),
                ThemeResponse.from(reservation.getTheme()), reservation.getDate(),
                TimeResponse.from(reservation.getTime()));
    }

    public static List<ReservationResponse> from(List<Reservation> reservations) {
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }
}
