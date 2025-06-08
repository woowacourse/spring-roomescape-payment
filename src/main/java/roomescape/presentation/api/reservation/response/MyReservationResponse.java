package roomescape.presentation.api.reservation.response;

import roomescape.application.reservation.query.dto.ReservationWithStatusAndPaymentResult;
import roomescape.application.reservation.query.dto.WaitingWithRankResult;
import roomescape.domain.reservation.ReservationStatus;

import java.time.LocalDate;

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

    public static MyReservationResponse from(final ReservationWithStatusAndPaymentResult result) {
        return new MyReservationResponse(
                result.reservationId(),
                result.themeName(),
                result.reservationDate(),
                ReservationDateTimeFormat.TIME.format(result.reservationTime()),
                toDisplayStatus(result.status()),
                ReservationResponseType.RESERVE,
                result.PaymentKey(),
                result.amount()
        );
    }

    public static MyReservationResponse from(final WaitingWithRankResult result) {
        return new MyReservationResponse(
                result.waitingId(),
                result.themeName(),
                result.reservationDate(),
                ReservationDateTimeFormat.TIME.format(result.reservationTime()),
                toDisplayStatus(result.waitingCount()),
                ReservationResponseType.WAITING,
                "대기 중인 예약은 결제할 수 없습니다",
                0L
        );
    }

    private static String toDisplayStatus(final ReservationStatus status) {
        return switch (status) {
            case RESERVE -> "예약";
        };
    }

    private static String toDisplayStatus(final long waitingCount) {
        return waitingCount + "번째 예약 대기";
    }
}
