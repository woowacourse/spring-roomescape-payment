package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.ReservationStatus;
import roomescape.domain.Waiting;
import roomescape.entity.Payment;
import roomescape.entity.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "예약 응답 DTO 입니다.")
public record ReservationDetailResponse(
        @Schema(description = "예약 ID입니다.")
        long reservationId,
        @Schema(description = "테마명입니다.")
        String theme,
        @Schema(description = "예약 날짜입니다.")
        LocalDate date,
        @Schema(description = "예약 시간입니다.")
        LocalTime time,
        @Schema(description = "예약 상태입니다.")
        String status,
        @Schema(description = "예약 결제 키 값입니다.")
        String paymentKey,
        @Schema(description = "결제 금액입니다.")
        Long amount
) {

    public static ReservationDetailResponse from(Waiting waiting) {
        Reservation reservation = waiting.getReservation();
        return toReservationDetailResponse(reservation, String.format(getStatusName(reservation.getStatus()), waiting.getRank()));
    }

    public static ReservationDetailResponse of(Reservation reservation, Payment payment) {
        if (reservation.isWaitingForPaymentStatus()) {
            return toReservationDetailResponse(reservation, getStatusName(reservation.getStatus()));
        }
        return new ReservationDetailResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getReservationTime().getStartAt(),
                getStatusName(reservation.getStatus()),
                payment.getPaymentKey(),
                payment.getTotalAmount());
    }

    private static ReservationDetailResponse toReservationDetailResponse(Reservation reservation, String status) {
        return new ReservationDetailResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getReservationTime().getStartAt(),
                status,
                null,
                null);
    }

    private static String getStatusName(ReservationStatus status) {
        return switch (status) {
            case BOOKED -> "예약";
            case WAITING -> "%d번째 예약대기";
            case WAITING_FOR_PAYMENT -> "결제 대기";
        };
    }
}
