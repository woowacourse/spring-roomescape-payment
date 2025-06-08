package roomescape.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Reservation;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "예약 정보 응답 DTO")
public record ReservationResponse(
        @Schema(description = "예약 ID")
        Long id,

        @Schema(description = "예약자 정보")
        MemberResponse member,

        @Schema(description = "예약 일자", example = "2024-03-20")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,

        @Schema(description = "예약 시간 정보")
        ReservationTimeResponse time,

        @Schema(description = "예약 테마 정보")
        ThemeResponse theme
) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                MemberResponse.from(reservation.getMember()),
                reservation.getDate(),
                ReservationTimeResponse.from(reservation.getTime()),
                ThemeResponse.from(reservation.getTheme())
        );
    }

    public static List<ReservationResponse> from(List<Reservation> reservations) {
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }
}
