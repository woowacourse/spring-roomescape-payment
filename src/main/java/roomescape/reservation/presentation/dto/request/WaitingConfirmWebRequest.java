package roomescape.reservation.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record WaitingConfirmWebRequest(@Schema(description = "예약 대기를 예약으로 바꿀 예약슬롯의 id") Long reservationSlotId,
                                       @Schema(description = "결제 승인에 필요한 결제 키") String paymentKey,
                                       @Schema(description = "결제 승인에 필요한 주문 번호") String orderId,
                                       @Schema(description = "결제 금액") Long amount) {
}
