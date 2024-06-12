package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationDetailResponse(
        @Schema(description = "예약 엔티티 식별자") long id,
        @Schema(description = "예약된 테마") String theme,
        @Schema(description = "예약된 날짜") LocalDate date,
        @Schema(description = "예약된 시간") LocalTime time,
        @Schema(description = "예약 혹은 대기 상태") String status,
        @Schema(description = "결제 키") String paymentKey,
        @Schema(description = "금액") BigDecimal amount
) {
    public static ReservationDetailResponse from(Reservation reservation, long index) {
        String paymentKey = checkPaymentKey(reservation);
        BigDecimal amount = checkAmount(reservation);

        return new ReservationDetailResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getReservationTime().getStartAt(),
                getStatusNameByIndex(index),
                paymentKey,
                amount
        );
    }

    private static String checkPaymentKey(Reservation reservation) {
        if (reservation.getPayment() == null) {
            return null;
        }
        return reservation.getPayment().getPaymentKey();
    }

    private static BigDecimal checkAmount(Reservation reservation) {
        if (reservation.getPayment() == null) {
            return null;
        }
        return reservation.getPayment().getAmount();
    }

    private static String getStatusNameByIndex(long index) {
        if (index == 1) {
            return "예약";
        }
        return String.format("%d번째 예약대기", index - 1);
    }
}
