package roomescape.dto;

import roomescape.domain.Reservation;
import roomescape.domain.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationDetailResponse(
        long id,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        String paymentKey,
        BigDecimal amount
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

    private static String getStatusName(ReservationStatus status) {
        return switch (status) {
            case BOOKED -> "예약";
            case WAITING -> "%d번째 예약대기";
        };
    }

    private static String getStatusNameByIndex(long index) {
        if (index == 1) {
            return "예약";
        }
        return String.format("%d번째 예약대기", index - 1);
    }
}
