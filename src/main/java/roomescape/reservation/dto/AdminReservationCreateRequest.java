package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

public record AdminReservationCreateRequest(
        @Schema(description = "예약자 id", example = "1")
        Long memberId,
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(description = "예약 날짜", example = "2024-06-22")
        LocalDate date,
        @Schema(description = "예약 시간 id", example = "1")
        Long timeId,
        @Schema(description = "예약 테마 id", example = "1")
        Long themeId) {
    public Reservation createReservation(Member member, ReservationTime time, Theme theme) {
        return new Reservation(member, date, time, theme, ReservationStatus.RESERVED);
    }
}
