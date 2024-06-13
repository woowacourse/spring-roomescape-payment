package roomescape.registration.domain.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.payment.domain.Payment;
import roomescape.registration.domain.reservation.domain.Reservation;

import java.time.LocalDate;

@Schema(description = "예약 생성 요청")
public record ReservationRequest(

        @Schema(description = "예약 일자", example = "2099-12-31")
        LocalDate date,

        @Schema(description = "테마 ID", example = "1")
        Long themeId,

        @Schema(description = "시간 ID", example = "2")
        Long timeId,

        @Schema(description = "결제 유형", example = "카드")
        String paymentType,

        @Schema(description = "결제 키", example = "receiveFromClient")
        String paymentKey,

        @Schema(description = "주문 번호", example = "randomString")
        String orderId,

        @Schema(description = "결제 금액", example = "55000")
        Integer amount) {

        public Payment toPayment(Reservation reservation) {
                return new Payment(paymentKey, orderId, reservation);
        }
}
