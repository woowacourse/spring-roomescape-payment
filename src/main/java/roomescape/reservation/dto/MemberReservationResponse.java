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
        String amount
) {
    public MemberReservationResponse(ReservationWithInformation reservation) {
        this(
                reservation.getReservationId(),
                reservation.getThemeName(),
                reservation.getReservationDate(),
                reservation.getStartAt(),
                statusMessage(reservation.getWaitingNumber()),
                paymentKey(reservation.getPaymentKey()),
                amount(reservation.getAmount())
        );
    }

    private static String statusMessage(int waitingNumber) {
        if (waitingNumber > 1) {
            return waitingNumber + "번째 예약 대기";
        }
        return "예약";
    }

    private static String paymentKey(String paymentKey) {
        if (paymentKey.isBlank()) {
            return "관리자에게 따로 문의";
        }
        return paymentKey;
    }

    private static String amount(int amount) {
        if (amount == 0) {
            return "관리자에게 따로 문의";
        }
        return Integer.toString(amount);
    }
}
