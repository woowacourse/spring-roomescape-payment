package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;

@Schema(description = "마이페이지 예약 목록 응답 객체")
public record MyPageReservationResponse(
        @Schema(description = "예약 ID", example = "1")
        Long reservationId,

        @Schema(description = "예약 테마명", example = "좀비 탈출")
        String theme,

        @Schema(description = "예약 날짜", example = "2025-06-15")
        LocalDate date,

        @Schema(description = "예약 시간", example = "14:30")
        LocalTime time,

        @Schema(description = "예약 상태", example = "예약 확정")
        String status,

        @Schema(description = "대기 순서 (대기 중인 경우)", example = "3")
        int priority,

        @Schema(description = "결제 정보")
        MyPaymentResponse payment
) {

    public static MyPageReservationResponse from(Reservation reservation, int priority) {
        return new MyPageReservationResponse(
                reservation.getId(),
                reservation.getReservationItem().getTheme().getName(),
                reservation.getReservationItem().getDate(),
                reservation.getReservationItem().getTime().getStartAt(),
                reservation.getReservationStatus().description,
                priority,
                null
        );
    }

    public static MyPageReservationResponse from(Reservation reservation, int priority, Payment payment) {
        return new MyPageReservationResponse(
                reservation.getId(),
                reservation.getReservationItem().getTheme().getName(),
                reservation.getReservationItem().getDate(),
                reservation.getReservationItem().getTime().getStartAt(),
                reservation.getReservationStatus().description,
                priority,
                new MyPaymentResponse(payment.getPaymentKey(), payment.getAmount())
        );
    }

    @Schema(description = "결제 정보")
    record MyPaymentResponse(
            @Schema(description = "결제 키", example = "example_payment_key")
            String paymentKey,

            @Schema(description = "결제 금액", example = "25000")
            int amount
    ) {
    }
}
