package roomescape.reservation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.MyReservation;

public record MyReservationResponse(
        Long id,
        String themeName,
        LocalDate date,
        LocalTime time,
        String status,
        String paymentKey,
        BigDecimal amount
) {

    public MyReservationResponse(MyReservation reservation) {
        this(
                reservation.getReservationId(),
                reservation.getThemeName(),
                reservation.getReservationDate(),
                reservation.getStartAt(),
                statusMessage(reservation.getWaitingNumber()),
                reservation.getPaymentKey(),
                reservation.getAmount()
        );
    }

    private static String statusMessage(Long waitingNumber) {
        if (waitingNumber > 1) {
            return waitingNumber + "번째 예약 대기";
        }
        return "예약";
    }
}
