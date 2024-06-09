package roomescape.reservation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import roomescape.member.dto.MemberResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.theme.dto.ThemeResponse;

@Schema(name = "예약 정보", description = "예약 저장 및 조회 응답에 사용됩니다.")
public record ReservationResponse(
        @Schema(description = "예약 번호. 예약을 식별할 때 사용합니다.")
        Long id,
        @Schema(description = "예약 날짜", type = "string", example = "2022-12-31")
        LocalDate date,
        @JsonProperty("member")
        @Schema(description = "예약한 회원 정보")
        MemberResponse member,
        @JsonProperty("time")
        @Schema(description = "예약 시간 정보")
        ReservationTimeResponse time,
        @JsonProperty("theme")
        @Schema(description = "예약한 테마 정보")
        ThemeResponse theme,
        @Schema(description = "예약 상태", type = "string")
        ReservationStatus status
) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getDate(),
                MemberResponse.fromEntity(reservation.getMember()),
                ReservationTimeResponse.from(reservation.getReservationTime()),
                ThemeResponse.from(reservation.getTheme()),
                reservation.getReservationStatus()
        );
    }
}
