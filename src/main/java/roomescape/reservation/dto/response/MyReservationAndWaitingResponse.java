package roomescape.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.waiting.domain.Waiting;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "내 예약 및 대기 응답")
public record MyReservationAndWaitingResponse(
        @Schema(description = "예약/대기 ID", example = "1")
        Long id,
        @Schema(description = "테마명", example = "기본 테마")
        String theme,
        @Schema(description = "예약 날짜", example = "2024-03-20")
        LocalDate date,
        @Schema(description = "예약 시간", example = "10:00")
        LocalTime time,
        @Schema(description = "상태", example = "예약")
        String status,
        @Schema(description = "결제 키", example = "payment_key_1234")
        String paymentKey,
        @Schema(description = "결제 금액", example = "10000")
        Long amount) {

    public static MyReservationAndWaitingResponse from(Reservation reservation) {
        if (reservation.getReservationStatus() != ReservationStatus.PENDING) {
            return new MyReservationAndWaitingResponse(
                    reservation.getId(),
                    reservation.getThemeName(),
                    reservation.getDate(),
                    reservation.getReservationTime(),
                    reservation.getReservationStatus().getStatus(),
                    reservation.getPayment().getPaymentKey(),
                    reservation.getPayment().getAmount()
            );
        }

        return new MyReservationAndWaitingResponse(
                reservation.getId(),
                reservation.getThemeName(),
                reservation.getDate(),
                reservation.getReservationTime(),
                reservation.getReservationStatus().getStatus(),
                null,
                null
        );
    }

    public static MyReservationAndWaitingResponse fromWaiting(Waiting waiting, long rank) {
        return new MyReservationAndWaitingResponse(
                waiting.getId(),
                waiting.getTheme().getName(),
                waiting.getDate(),
                waiting.getTime().getStartAt(),
                String.valueOf(rank),
                null,
                null);
    }
}
