package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.PaymentResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.waiting.domain.WaitingWithOrder;

public record MyReservationWaitingResponse(
        Long ownerId,
        String themeName,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        @JsonFormat(pattern = "HH:mm")
        @Schema(type = "String", pattern = "HH:mm")
        LocalTime startAt,
        String status,
        PaymentResponse payment) {
    private static final String WAITING_STATUS = "%d번째 예약대기";

    public static MyReservationWaitingResponse from(Reservation reservation, Payment payment) {
        return new MyReservationWaitingResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                reservation.getReservationStatus().getName(),
                new PaymentResponse(
                        payment.getPaymentKey(),
                        payment.getTotalAmount()
                ));
    }

    public static MyReservationWaitingResponse from(Reservation reservation) {
        return new MyReservationWaitingResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                reservation.getReservationStatus().getName(),
                null);
    }

    public static MyReservationWaitingResponse from(WaitingWithOrder waitingWithOrder) {
        return new MyReservationWaitingResponse(
                waitingWithOrder.getWaiting().getId(),
                waitingWithOrder.getWaiting().getReservation().getTheme().getName(),
                waitingWithOrder.getWaiting().getReservation().getDate(),
                waitingWithOrder.getWaiting().getReservation().getTime().getStartAt(),
                java.lang.String.format(WAITING_STATUS, waitingWithOrder.getOrder()),
                null);
    }
}
