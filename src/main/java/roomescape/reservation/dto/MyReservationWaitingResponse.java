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
        @Schema(description = "예약/대기 id", example = "1")
        Long ownerId,
        @Schema(description = "테마 이름", example = "레벨 1 탈출")
        String themeName,
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(description = "예약 날짜", example = "2024-06-22")
        LocalDate date,
        @JsonFormat(pattern = "HH:mm")
        @Schema(description = "예약 시간", type = "String", pattern = "HH:mm", example = "23:00")
        LocalTime startAt,
        @Schema(description = "예약 상태", example = "예약 확정")
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
