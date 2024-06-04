package roomescape.dto;

import roomescape.domain.Reservation;
import roomescape.domain.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationDetailResponse(
        long id,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        String paymentKey,
        long amount
) {
    public static ReservationDetailResponse from(Reservation reservation, ReservationStatus status) {
        return new ReservationDetailResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getReservationTime().getStartAt(),
                getStatusName(status)

                , reservation.getPaymentKey(), reservation.getAmount()


//                , "paymentKeySample",400
        );
    }

    public static ReservationDetailResponse from(Reservation reservation, long index) {
        return new ReservationDetailResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getReservationTime().getStartAt(),
                getStatusNameByIndex(index)


                , reservation.getPaymentKey(), reservation.getAmount()
//                , "paymentKeySample",400
        );
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
