package roomescape.reservation.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import roomescape.reservation.domain.Reservation;

@Schema(description = "내 예약 조회 응답 정보")
public record MyReservationResponse(

        @Schema(description = "예약 ID", example = "42")
        Long reservationId,

        @Schema(description = "예약한 테마 이름", example = "감옥에서 탈출하기")
        String theme,

        @Schema(description = "예약 날짜", example = "2025-07-01")
        LocalDate date,

        @Schema(description = "예약 시간", example = "15:00")
        @JsonFormat(pattern = "HH:mm")
        LocalTime time,

        @Schema(description = "예약 상태", example = "예약")
        String status,

        @Schema(description = "결제 키", example = "test_payment_key_abcdefg")
        String paymentKey,

        @Schema(description = "결제 금액", example = "22000")
        Long amount

) {
    public static MyReservationResponse from(final Reservation reservation) {
        return new MyReservationResponse(
                reservation.getId(),
                reservation.getTheme().getName().name(),
                reservation.getDate(),
                reservation.getStartAt(),
                "예약",
                reservation.getPaymentKeyasString(),
                reservation.getAmountAsLong()
        );
    }

    public static List<MyReservationResponse> from(final List<Reservation> reservations) {
        return reservations.stream()
                .map(MyReservationResponse::from)
                .toList();
    }
}
