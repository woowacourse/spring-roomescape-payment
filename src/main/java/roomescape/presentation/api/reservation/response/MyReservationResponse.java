package roomescape.presentation.api.reservation.response;

import java.time.LocalDate;
import roomescape.application.reservation.query.dto.ReservationWithStatusResult;
import roomescape.application.reservation.query.dto.WaitingWithRankResult;
import roomescape.domain.reservation.ReservationStatus;

public record MyReservationResponse(
        Long id,
        String theme,
        LocalDate date,
        String time,
        String status,
        ReservationResponseType type,
        String paymentKey,
        Long amount
) {

    public static MyReservationResponse from(ReservationWithStatusResult reservationWithStatusResult,
                                             String paymentKey,
                                             Long amount) {
        return new MyReservationResponse(
                reservationWithStatusResult.reservationId(),
                reservationWithStatusResult.themeName(),
                reservationWithStatusResult.reservationDate(),
                ReservationDateTimeFormat.TIME.format(reservationWithStatusResult.reservationTime()),
                toDisplayStatus(reservationWithStatusResult.status()),
                ReservationResponseType.RESERVE,
                paymentKey,
                amount
        );
    }

    public static MyReservationResponse from(WaitingWithRankResult waitingWithRankResult) {
        return new MyReservationResponse(
                waitingWithRankResult.waitingId(),
                waitingWithRankResult.themeName(),
                waitingWithRankResult.reservationDate(),
                ReservationDateTimeFormat.TIME.format(waitingWithRankResult.reservationTime()),
                toDisplayStatus(waitingWithRankResult.waitingCount()),
                ReservationResponseType.WAITING,
                null,
                null
        );
    }

    private static String toDisplayStatus(ReservationStatus status) {
        return switch (status) {
            case RESERVE -> "예약";
            case CANCEL -> "취소";
        };
    }

    private static String toDisplayStatus(long waitingCount) {
        return waitingCount + "번째 예약 대기";
    }
}
