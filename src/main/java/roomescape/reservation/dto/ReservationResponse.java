package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import roomescape.member.dto.MemberResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.theme.dto.ThemeResponse;
import roomescape.time.dto.TimeResponse;

public record ReservationResponse(
        @Schema(description = "예약 id", example = "1")
        Long id,
        MemberResponse member,
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(description = "예약 날짜", example = "2024-06-22")
        LocalDate date,
        TimeResponse time,
        ThemeResponse theme,
        @Schema(description = "예약 상태", example = "예약 확정")
        String status) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                MemberResponse.from(reservation.getMember()),
                reservation.getDate(),
                TimeResponse.from(reservation.getTime()),
                ThemeResponse.from(reservation.getTheme()),
                reservation.getReservationStatus().getName());
    }
}
