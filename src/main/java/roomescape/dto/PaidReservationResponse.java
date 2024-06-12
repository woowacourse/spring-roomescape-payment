package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import roomescape.domain.ReservationStatus;

public record PaidReservationResponse(
        @Schema(description = "예약 ID")
        long id,

        @Schema(description = "예약자 이름")
        String name,

        @Schema(description = "예약 날짜")
        LocalDate date,

        @Schema(description = "예약 시간")
        ReservationTimeResponse time,

        @Schema(description = "테마 정보")
        ThemeResponse theme,

        @Schema(description = "예약 상태")
        ReservationStatus status,

        @Schema(description = "결제 키")
        String paymentKey,

        @Schema(description = "주문 ID")
        String orderId,

        @Schema(description = "결제 금액")
        long amount
) {
    public static PaidReservationResponse of(ReservationResponse reservationResponse, PaymentResponse paymentResponse) {
        return new PaidReservationResponse(
                reservationResponse.id(),
                reservationResponse.name(),
                reservationResponse.date(),
                reservationResponse.time(),
                reservationResponse.theme(),
                reservationResponse.status(),
                paymentResponse.paymentKey(),
                paymentResponse.orderId(),
                paymentResponse.totalAmount()
        );
    }
}
