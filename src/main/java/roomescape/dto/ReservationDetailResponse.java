package roomescape.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationStatus;

public record ReservationDetailResponse(
        long id,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        String paymentKey,
        Long amount
) {

    public static ReservationDetailResponse from(Reservation reservation, long index) {

        return new ReservationDetailResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getReservationTime().getStartAt(),
                getStatusName(reservation, index),
                reservation.getPaymentKey().orElse(null),
                reservation.getAmount().orElse(null)
        );
    }

    private static String getStatusName(Reservation reservation, long index) {
        ReservationStatus status = reservation.getReservationStatus();
        return switch (status) {
            case BOOKED -> {
                if (reservation.getPayment().isEmpty()) {
                    yield "결제 대기";
                }
                yield "결제 완료";
            }
            case WAITING -> String.format("%d번째 예약대기", index - 1);
        };
    }
}
