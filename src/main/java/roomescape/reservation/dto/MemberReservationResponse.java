package roomescape.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.ReservationWithInformation;

public record MemberReservationResponse(
        Long id,
        String themeName,
        LocalDate date,
        LocalTime time,
        String status,
        String paymentKey,
        int amount
) {
    public MemberReservationResponse(ReservationWithInformation reservation) {
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

    private static String statusMessage(int waitingNumber) {
        if (waitingNumber > 1) {
            return waitingNumber + "번째 예약 대기";
        }
        return "예약";
    }
}
