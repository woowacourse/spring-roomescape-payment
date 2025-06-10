package roomescape.domain.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.payment.entity.Payment;
import roomescape.domain.reservation.Status;
import roomescape.domain.reservation.entity.Reservation;
import roomescape.domain.waiting.entity.Waiting;

@Schema(description = "마이페이지 예약 응답 DTO")
public record ReservationMineResponse(
        @Schema(description = "테마 이름", example = "우주 테마")
        String theme,

        @Schema(description = "예약 날짜", example = "2026-10-01")
        LocalDate date,

        @Schema(description = "예약 시간", example = "14:00")
        @JsonFormat(pattern = "HH:mm") LocalTime time,

        @Schema(description = "예약 상태", example = "RESERVED")
        String status,

        @Schema(description = "결제 키", example = "pay_1234567890")
        String paymentKey,

        @Schema(description = "결제 금액", example = "20000")
        int amount
) {

    public static ReservationMineResponse from(final Reservation reservation, final Payment payment) {
        return new ReservationMineResponse(
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                Status.RESERVED.displayName(),
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }


    public static ReservationMineResponse from(final Waiting waiting, final Long order) {
        return new ReservationMineResponse(
                waiting.getTheme().getName(),
                waiting.getDate(),
                waiting.getTime().getStartAt(),
                order + Status.WAITING.displayName(),
                null,
                0
        );
    }
}
