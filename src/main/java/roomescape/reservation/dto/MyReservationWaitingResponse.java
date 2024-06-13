package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.paymenthistory.dto.PaymentResponse;
import roomescape.waiting.domain.WaitingWithOrder;

public record MyReservationWaitingResponse(
        Long ownerId,
        String themeName,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        @JsonFormat(pattern = "HH:mm")
        LocalTime startAt,
        String paymentKey,
        long amount,
        String status
) {
    private static final String RESERVATION_STATUS = "예약";
    private static final String WAITING_STATUS = "%d번째 예약대기";
    private static final String EMPTY_PARAMETER_KEY = null;
    private static final int NOT_PAID = 0;

    public static MyReservationWaitingResponse from(MyReservationResponse reservationResponse,
                                                    PaymentResponse paymentResponse) {
        return new MyReservationWaitingResponse(
                reservationResponse.ownerId(),
                reservationResponse.themeName(),
                reservationResponse.date(),
                reservationResponse.startAt(),
                paymentResponse.paymentKey(),
                paymentResponse.amount(),
                RESERVATION_STATUS);
    }

    public static MyReservationWaitingResponse from(MyReservationResponse reservationResponse) {
        return new MyReservationWaitingResponse(
                reservationResponse.ownerId(),
                reservationResponse.themeName(),
                reservationResponse.date(),
                reservationResponse.startAt(),
                EMPTY_PARAMETER_KEY,
                NOT_PAID,
                reservationResponse.reservationStatus().getName());
    }

    public static MyReservationWaitingResponse from(WaitingWithOrder waitingWithOrder) {
        return new MyReservationWaitingResponse(
                waitingWithOrder.getWaiting().getId(),
                waitingWithOrder.getWaiting().getReservation().getTheme().getName(),
                waitingWithOrder.getWaiting().getReservation().getDate(),
                waitingWithOrder.getWaiting().getReservation().getTime().getStartAt(),
                EMPTY_PARAMETER_KEY,
                NOT_PAID,
                java.lang.String.format(WAITING_STATUS, waitingWithOrder.getOrder()));
    }
}
