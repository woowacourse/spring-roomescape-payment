package roomescape.controller.dto.response;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;

public record ReservationResponse(
        @Schema(description = "예약 고유 번호", example = "1")
        Long id,
        @Schema(description = "예약자 정보")
        MemberResponse member,
        @Schema(description = "예약 날짜", example = "2024-06-08")
        LocalDate date,
        @Schema(description = "예약 시간")
        TimeResponse time,
        @Schema(description = "예약 테마")
        ThemeResponse theme,
        @Schema(description = "결제 정보")
        PaymentResponse payment,
        @Schema(description = "예약 상태", example = "RESERVED")
        ReservationStatus status) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                MemberResponse.from(reservation.getMember()),
                reservation.getDate(),
                TimeResponse.from(reservation.getTime()),
                ThemeResponse.from(reservation.getTheme()),
                PaymentResponse.from(reservation.getPayment()),
                reservation.getStatus()
        );
    }
}
