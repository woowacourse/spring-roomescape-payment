package roomescape.application.dto.response.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.ReservationWithRank;

@Schema(name = "예약 정보")
public record UserReservationResponse(
        @Schema(description = "예약 ID", example = "1")
        Long reservationId,

        @Schema(description = "테마 이름", example = "테마 이름")
        String theme,

        @Schema(description = "예약 날짜", example = "2024-08-01", type = "string", format = "date")
        LocalDate date,

        @Schema(description = "예약 시간", example = "10:00", type = "string", format = "time")
        LocalTime time,

        @Schema(description = "예약 상태", example = "RESERVED")
        String status,

        @Schema(description = "순위", example = "1")
        Long rank,

        @Schema(description = "결제 키", example = "paymentKey")
        String paymentKey,

        @Schema(description = "결제 금액", example = "10000")
        Long amount
) {

    public static UserReservationResponse from(ReservationWithRank reservation) {
        return new UserReservationResponse(
                reservation.reservation().getId(),
                reservation.reservation().getDetail().getTheme().getName(),
                reservation.reservation().getDetail().getDate(),
                reservation.reservation().getDetail().getTime().getStartAt(),
                reservation.reservation().getStatus().name(),
                reservation.rank(),
                getPayment(reservation),
                getAmount(reservation));
    }

    private static String getPayment(ReservationWithRank reservation) {
        if (reservation.isPaid()) {
            return reservation.reservation().getPayment().getPaymentKey();
        }
        return "";
    }

    private static Long getAmount(ReservationWithRank reservation) {
        if (reservation.isPaid()) {
            return reservation.reservation().getPayment().getAmount();
        }
        return 0L;
    }
}
